package br.unb.cic.igor.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import br.unb.cic.igor.MainActivity
import br.unb.cic.igor.R
import br.unb.cic.igor.adapters.AdventuresAdapter
import br.unb.cic.igor.classes.Adventure
import br.unb.cic.igor.extensions.toList


class AdventuresFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var rv : RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun onRefresh() {
        var adventures : List<Adventure> = ArrayList()
        Adventure.List().addOnSuccessListener {
            if (it != null) {
                adventures = it.toList(Adventure::class.java)
                runAnimation(rv, adventures)
            }
        }
        runAnimation(rv, adventures)
        mSwipeRefreshLayout.setRefreshing(false)

    }


    companion object {
        fun newInstance() = AdventuresFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_adventures, container, false)

        var recyclerView: RecyclerView?
        recyclerView = view.findViewById(R.id.adventures_recycler_view)
        rv = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        var adventures : List<Adventure>
        Adventure.List().addOnSuccessListener {
            if (it != null) {
                adventures = it.toList(Adventure::class.java)
                runAnimation(recyclerView, adventures)
            }
        }

        // SwipeRefreshLayout
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)

        val fab: View = view.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
        }

        return view
    }

    private fun runAnimation(recyclerView: RecyclerView, adventures: List<Adventure>) {
        var animationController : LayoutAnimationController = AnimationUtils.loadLayoutAnimation(recyclerView.context, R.anim.layout_fall)

        recyclerView.adapter = AdventuresAdapter(adventures, context, activity as MainActivity)

        recyclerView.layoutAnimation = animationController
        (recyclerView.adapter as AdventuresAdapter).notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()

    }

    interface OnAdventureSelected {
        fun onAdventureSelected(adventureId: String)
    }

}
