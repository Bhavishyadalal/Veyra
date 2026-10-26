package app.veyra.music

import app.veyra.music.data.LatestRequestGate
import app.veyra.music.data.LikeState
import app.veyra.music.data.YtMusicRepository
import app.veyra.music.data.model.Song
import app.veyra.music.data.model.LikeStatus
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.After
import org.junit.Test

class RequestOwnershipTest {
    @After fun reset() {LikeState.clear()}

    @Test fun failedEarlierRatingCannotUndoAPlayerOrNotificationEdit() {
        val old = LikeState.beginEdit("track", LikeStatus.LIKE)
        val current = LikeState.beginEdit("track", LikeStatus.DISLIKE)
        assertFalse(LikeState.rollback(old, LikeStatus.INDIFFERENT))
        assertEquals(LikeStatus.DISLIKE, LikeState.overrides.value["track"])
        assertTrue(LikeState.isCurrent(current))
        assertTrue(LikeState.rollback(current, LikeStatus.LIKE))
        assertEquals(LikeStatus.INDIFFERENT, LikeState.overrides.value["track"])
        assertFalse(LikeState.rollback(current, LikeStatus.INDIFFERENT))
    }

    @Test fun returningToTheSameRatingStillInvalidatesTheOlderRequest() {
        val first = LikeState.beginEdit("track", LikeStatus.LIKE)
        LikeState.beginEdit("track", LikeStatus.INDIFFERENT)
        val last = LikeState.beginEdit("track", LikeStatus.LIKE)
        assertFalse(LikeState.isCurrent(first))
        assertFalse(LikeState.rollback(first, LikeStatus.INDIFFERENT))
        assertTrue(LikeState.isCurrent(last))
    }

    @Test fun signingOutInvalidatesOldRatingEvenAfterTheSameSongIsRatedAgain() {
        val old = LikeState.beginEdit("track", LikeStatus.LIKE)
        LikeState.clear()
        val current = LikeState.beginEdit("track", LikeStatus.DISLIKE)
        assertFalse(LikeState.rollback(old, LikeStatus.INDIFFERENT))
        assertTrue(LikeState.isCurrent(current))
        assertEquals(LikeStatus.DISLIKE, LikeState.overrides.value["track"])
    }

    @Test fun directRatingUpdateAlsoInvalidatesAnEarlierOptimisticEdit() {
        val old = LikeState.beginEdit("track", LikeStatus.LIKE)
        LikeState.set("track", LikeStatus.DISLIKE)
        assertFalse(LikeState.rollback(old, LikeStatus.INDIFFERENT))
        assertEquals(LikeStatus.DISLIKE, LikeState.overrides.value["track"])
    }

    @Test fun failedNewerRatingRestoresTheLastServerConfirmedValue() {
        val first = LikeState.beginEdit("track", LikeStatus.LIKE)
        val second = LikeState.beginEdit("track", LikeStatus.DISLIKE)
        LikeState.confirm(first, LikeStatus.LIKE)
        assertTrue(LikeState.rollback(second, LikeStatus.LIKE))
        assertEquals(LikeStatus.LIKE, LikeState.overrides.value["track"])
    }

    @Test fun twoFailedRapidRatingsRestoreTheOriginalLibraryValue() {
        val first = LikeState.beginEdit("track", LikeStatus.INDIFFERENT, LikeStatus.LIKE)
        val second = LikeState.beginEdit("track", LikeStatus.DISLIKE, LikeStatus.INDIFFERENT)
        assertFalse(LikeState.rollback(first, LikeStatus.LIKE))
        assertTrue(LikeState.rollback(second, LikeStatus.INDIFFERENT))
        assertEquals(LikeStatus.LIKE, LikeState.overrides.value["track"])
    }

    @Test fun lateConfirmationCannotRepopulateAnotherSessionOrReplaceANewerConfirmation() {
        val old = LikeState.beginEdit("track", LikeStatus.LIKE)
        LikeState.clear()
        val current = LikeState.beginEdit("track", LikeStatus.DISLIKE)
        LikeState.confirm(old, LikeStatus.LIKE)
        assertTrue(LikeState.rollback(current, LikeStatus.INDIFFERENT))
        assertEquals(LikeStatus.INDIFFERENT, LikeState.overrides.value["track"])
        val first = LikeState.beginEdit("track", LikeStatus.LIKE)
        val second = LikeState.beginEdit("track", LikeStatus.DISLIKE)
        LikeState.confirm(second, LikeStatus.DISLIKE)
        LikeState.confirm(first, LikeStatus.LIKE)
        val failed = LikeState.beginEdit("track", LikeStatus.INDIFFERENT)
        LikeState.rollback(failed, LikeStatus.LIKE)
        assertEquals(LikeStatus.DISLIKE, LikeState.overrides.value["track"])
    }

    @Test fun olderLibraryResponseCannotReplaceNewerResultOrPublishAfterInvalidation() = runBlocking {
        val gate=LatestRequestGate()
        val first=CompletableDeferred<String>()
        val values=mutableListOf<String>()
        val old=launch(start=CoroutineStart.UNDISPATCHED) {gate.load({first.await()},{values+=it})}
        gate.load({"new"},{values+=it})
        first.complete("old");old.join()
        assertEquals(listOf("new"),values)
        val next=CompletableDeferred<String>()
        val invalidated=launch(start=CoroutineStart.UNDISPATCHED) {gate.load({next.await()},{values+=it})}
        gate.invalidate();next.complete("previous account");invalidated.join()
        assertEquals(listOf("new"),values)
    }

    @Test fun cancelledNonCooperativeResponseCannotPublish() = runBlocking {
        val gate=LatestRequestGate()
        val answer=CompletableDeferred<String>()
        val values=mutableListOf<String>()
        val task=launch(start=CoroutineStart.UNDISPATCHED) {
            gate.load({withContext(NonCancellable){answer.await()}},{values+=it})
        }
        task.cancel();answer.complete("late");task.join()
        assertTrue(values.isEmpty())
    }

    @Test fun cancelledLikedPaginationCannotRepopulateRatingsAfterAccountClear() = runBlocking {
        LikeState.clear()
        val answer=CompletableDeferred<YtMusicRepository.SongPage>()
        val task=launch(start=CoroutineStart.UNDISPATCHED) {
            YtMusicRepository.syncLikedMusic("old") {withContext(NonCancellable){answer.await()}}
        }
        task.cancel();LikeState.clear()
        answer.complete(YtMusicRepository.SongPage(listOf(Song("old-account","Old","Artist",null)),null))
        task.join()
        assertTrue(LikeState.overrides.value.isEmpty())
    }

    @Test fun concurrentRatingAndSeedUpdatesPreserveEveryExplicitRating() = runBlocking {
        LikeState.clear()
        coroutineScope {
            repeat(300) {i->
                launch(Dispatchers.Default) {LikeState.set("track-$i",LikeStatus.DISLIKE)}
                launch(Dispatchers.Default) {LikeState.seedLiked(setOf("track-$i"))}
            }
        }
        assertEquals(300,LikeState.overrides.value.size)
        assertTrue(LikeState.overrides.value.values.all {it==LikeStatus.DISLIKE})
    }
}
