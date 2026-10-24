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
