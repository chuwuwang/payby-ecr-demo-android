package com.payby.pos.ecr.ui

import android.app.Activity
import com.payby.pos.ecr.databinding.ActivityResultBinding
import com.payby.pos.ecr.ui.base.ViewBindingActivity

class ResultActivity : ViewBindingActivity<ActivityResultBinding>() {

  companion object{
    fun start(activity: Activity,receive:String){
      val intent = android.content.Intent(activity,ResultActivity::class.java)
      intent.putExtra("receive",receive)
      activity.startActivity(intent)
    }
  }
  override fun init() {

    binding.receive.text = intent.getStringExtra("receive")
  }
}