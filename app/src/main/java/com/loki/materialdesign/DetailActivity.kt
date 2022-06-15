package com.loki.materialdesign

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.transition.Transition
import com.loki.materialdesign.databinding.ActivityDetailBinding
import java.util.ArrayList

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailBinding

    companion object {
        val EXTRA_PARAM_ID = "place_id"

        fun newIntent(context: Context, position: Int): Intent {

            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_PARAM_ID, position)

            return intent
        }
    }

    private lateinit var inputManager: InputMethodManager
    private lateinit var place: Place
    private lateinit var todoList: ArrayList<String>
    private lateinit var toDoAdapter: ArrayAdapter<*>

    private var isEditTextVisible: Boolean = false
    private var defaultColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupValues()
        setUpAdapter()
        loadPlace()
        windowTransition()
        getPhoto()
    }

    private fun setupValues() {
        place = PlaceData.placeList()[intent.getIntExtra(EXTRA_PARAM_ID, 0)]
        binding.addButton.setOnClickListener(this)
        defaultColor = ContextCompat.getColor(this, R.color.primary_dark)
        inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.revealView.visibility = View.INVISIBLE
        isEditTextVisible = false
    }

    private fun setUpAdapter() {
        todoList = ArrayList()
        toDoAdapter = ArrayAdapter(this, R.layout.row_todo, todoList)
        binding.activitiesList.adapter = toDoAdapter
    }

    private fun loadPlace() {
        binding.placeTitle.text = place.name
        binding.placeImage.setImageResource(place.getImageResourceId(this))
    }

    private fun windowTransition() {

        window.enterTransition.addListener(object : android.transition.Transition.TransitionListener{

            override fun onTransitionStart(p0: android.transition.Transition?) {}

            override fun onTransitionEnd(p0: android.transition.Transition?) {
                binding.addButton.animate().alpha(1.0f)
                window.enterTransition.removeListener(this)
            }

            override fun onTransitionCancel(p0: android.transition.Transition?) { }

            override fun onTransitionPause(p0: android.transition.Transition?) { }

            override fun onTransitionResume(p0: android.transition.Transition?) { }
        })

    }

    private fun addToDo(todo: String) {
        todoList.add(todo)
    }

    private fun getPhoto() {
        val photo = BitmapFactory.decodeResource(resources, place.getImageResourceId(this))

        val palette = Palette.from(photo).generate()
        applyPalette(palette)
        colorize(photo)
    }


    private fun colorize(photo: Bitmap) {}

    private fun applyPalette(palette: Palette) {

        window.setBackgroundDrawable(ColorDrawable(palette.getDarkMutedColor(defaultColor)))
        binding.placeNameHolder.setBackgroundColor(palette.getMutedColor(defaultColor))
        binding.revealView.setBackgroundColor(palette.getLightVibrantColor(defaultColor))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addButton -> if (!isEditTextVisible) {
                revealEditText(binding.revealView)
                binding.todoText.requestFocus()
                inputManager.showSoftInput(binding.todoText, InputMethodManager.SHOW_IMPLICIT)

                binding.addButton.setImageResource(R.drawable.icn_morph)
                val animatable = binding.addButton.drawable as Animatable
                animatable.start()

            } else {
                addToDo(binding.todoText.text.toString())
                toDoAdapter.notifyDataSetChanged()
                inputManager.hideSoftInputFromWindow(binding.todoText.windowToken, 0)
                hideEditText(binding.revealView)

                binding.addButton.setImageResource(R.drawable.icn_morph_reverse)
                val animatable = binding.addButton.drawable as Animatable
                animatable.start()
            }
        }
    }

    private fun revealEditText(view: LinearLayout) {

        val cx = view.right - 30
        val cy = view.bottom - 60
        val finalRadius = Math.max(view.width, view.height)
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat())
        view.visibility = View.VISIBLE
        isEditTextVisible = true
        anim.start()
    }

    private fun hideEditText(view: LinearLayout) {

        val cx = view.right - 30
        val cy = view.bottom - 60
        val initialRadius = view.width
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius.toFloat(), 0f)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.INVISIBLE
            }
        })
        isEditTextVisible = false
        anim.start()
    }

    override fun onBackPressed() {
        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration = 100
        binding.addButton.startAnimation(alphaAnimation)
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                binding.addButton.visibility = View.GONE
                finishAfterTransition()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }
}