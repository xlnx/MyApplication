package com.example.myapplication

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.VocabCardViewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.varunest.sparkbutton.SparkButton
import com.varunest.sparkbutton.SparkEventListener

class VocabCardView : LinearLayout {

    val binding: VocabCardViewBinding

    constructor(context: Context, vocab: Vocab): super(context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.vocab_card_view, this, true)
        binding.vocab = vocab

        findViewById<SparkButton>(R.id.starButton).apply {
            isChecked = vocab.star
            setEventListener(object: SparkEventListener {
                override fun onEvent(button: ImageView?, buttonState: Boolean) {
//                binding.vocab!!.content = "asdasdasd"
//                binding.invalidateAll()
                    binding.vocab!!.star = buttonState
                    GlobalResource.db.vocabDao().update(binding.vocab!!)
                }
                override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {}
                override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {}
            })
        }

        findViewById<FloatingActionButton>(R.id.speakButton).apply {
            setOnClickListener({_ ->
                GlobalResource.tts.speak(vocab.content, TextToSpeech.QUEUE_FLUSH, null)
            })
        }
    }
}