package app.veyra.music

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import app.veyra.music.playback.EqLayout
import app.veyra.music.playback.EqualizerProcessor
import app.veyra.music.playback.SpatialAudioProcessor
import app.veyra.music.playback.TransitionFilterProcessor
import app.veyra.music.playback.audio.AudioBlock
import app.veyra.music.playback.manualCurve
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Seeking/reconfiguration must not leak the previous stream's DSP history. */
class AudioProcessorFlushTest {
    private val format = AudioProcessor.AudioFormat(48_000, 2, C.ENCODING_PCM_16BIT)

    @Test fun equalizerHistoryClearsThroughModernFlushCallback() {
        val processor = EqualizerProcessor()
        processor.setTuning(true, manualCurve(List(EqLayout.MANUAL_COUNT) { 6f }), 0f)
        checkFlush(processor, processor::process)
    }

    @Test fun spatialHistoryClearsThroughModernFlushCallback() {
        val processor = SpatialAudioProcessor().apply { enabled = true }
        checkFlush(processor, processor::process)
    }

    @Test fun transitionHistoryClearsThroughModernFlushCallback() {
        val processor = TransitionFilterProcessor().apply { setCutoffs(1_200f, 100f) }
        checkFlush(processor, processor::process)
    }

    @Test fun spatialFloatReconfigurationClearsCrossfeedEvenWhenFormatIsUnchanged() {
        val processor = SpatialAudioProcessor().apply { enabled = true; configure(48_000, 2) }
        processor.process(block(signal = true))
        val tail = block(signal = false)
        processor.process(tail)
        assertTrue("The test must first populate the crossfeed delay", tail.samples.any { abs(it) > 1e-6f })
        processor.process(block(signal = true))
        processor.configure(48_000, 2)
        val silence = block(signal = false)
        processor.process(silence)
        assertSilent(silence)
    }

    private fun checkFlush(processor: BaseAudioProcessor, process: (AudioBlock) -> Unit) {
        assertEquals("Exercise an active Media3 PCM processor", format, processor.configure(format))
        processor.flush(AudioProcessor.StreamMetadata.DEFAULT)
        process(block(signal = true))
        val tail = block(signal = false)
        process(tail)
        assertTrue("Populate DSP history before checking its reset", tail.samples.any { abs(it) > 1e-6f })
        process(block(signal = true))
        processor.flush(AudioProcessor.StreamMetadata.DEFAULT)
        val silence = block(signal = false)
        process(silence)
        assertSilent(silence)
    }

    private fun block(signal: Boolean) = AudioBlock(channelCount = 2, capacityFrames = 512).apply {
        reset(512)
        if (signal) for (frame in 0 until frameCount) samples[frame * 2] = 0.2f
    }

    private fun assertSilent(block: AudioBlock) {
        assertTrue("Previous-stream samples survived reset", block.samples.all { abs(it) <= 1e-8f })
    }
}
