package br.unb.cic.igor.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import br.unb.cic.igor.R
import br.unb.cic.igor.classes.Player
import br.unb.cic.igor.classes.Session
import kotlinx.android.synthetic.main.fragment_adventure_tabs.*
import kotlinx.android.synthetic.main.fragment_adventure_tabs.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AdventureTabsFragment.OnTabSelectionListener] interface
 * to handle interaction events.
 * Use the [AdventureTabsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AdventureTabsFragment : Fragment(), AdventureFragment.OnSessionSelectedListener, PlayersFragment.OnPlayersFragmentInteractionListener {
    private var state: State = State.ADVENTURE
    private var adventureFragment: Fragment = AdventureFragment.newInstance()
    private var playersFragment: Fragment = PlayersFragment.newInstance(1)
    private var currentFragment: Fragment = adventureFragment
    private var selectedSession: Session? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handles action bar item clicks here.
        when (item.itemId) {
            R.id.action_editar -> {
                when (state) {
                    State.SESSION ->
                        stateTransition(State.SESSION_EDIT, SessionEditFragment.newInstance(selectedSession!!))
                    State.ADVENTURE ->
                        stateTransition(State.ADV_EDIT, AdventureEditFragment.newInstance())
                    else ->
                        toast("invalid state")
                }

                return true
            }
            R.id.action_ordenar -> {
                toast("${resources.getString(R.string.ordenar)} $state")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun toast(message: String) {
        if (activity != null) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_adventure_tabs, container, false)
        // Inflate the layout for this fragment

        view.playersTab.setOnClickListener {
            if (state != State.PLAYERS) {
                stateTransition(State.PLAYERS, playersFragment)
            }
        }

        view.adventureTab.setOnClickListener {
            if (state != State.ADVENTURE) {
                stateTransition(State.ADVENTURE, adventureFragment)
            }
        }

        view.addButton.setOnClickListener {
            onAddButtonPressed()
        }

        val ft = fragmentManager?.beginTransaction();
        ft?.replace(R.id.contentFrame, adventureFragment);
        ft?.commit()

        return view
    }

    private fun onAddButtonPressed() {
        when (state) {
            State.ADVENTURE -> {
                stateTransition(State.SESSION_CREATE, AddSessionFragment())
            }
            State.PLAYERS -> {
                toast("Players state")
            }
            else -> toast("wrong state")
        }
    }

    fun stateTransition(nextState : State, fragment : Fragment) {
        when (nextState) {
            State.SESSION_CREATE, State.SESSION_EDIT, State.PLAYER_DETAILS -> {
                addButton.visibility = View.INVISIBLE
                setHasOptionsMenu(false)
            }
            State.ADVENTURE -> {
                contentView.setImageResource(R.drawable.adventure_progress_tab)
                addButton.visibility = View.VISIBLE
                setHasOptionsMenu(true)
                addButton.setImageResource(R.drawable.add_sesssion)
            }
            State.PLAYERS -> {
                contentView.setImageResource(R.drawable.players_tab)
                addButton.visibility = View.VISIBLE
                setHasOptionsMenu(true)
                addButton.setImageResource(R.drawable.add_player)
            }
            State.SESSION -> {
                addButton.visibility = View.INVISIBLE
                setHasOptionsMenu(true)
            }
            State.ADV_EDIT -> {
                addButton.visibility = View.INVISIBLE
                setHasOptionsMenu(false)
            }
        }

        switchContent(fragment)
        state = nextState
    }

    private fun switchContent(contentFragment: Fragment) {
        currentFragment = contentFragment

        val ft = fragmentManager?.beginTransaction()

        ft?.replace(R.id.contentFrame, contentFragment)

        ft?.commit()
    }

    fun onBackPressed() {
        when (state) {
            State.SESSION, State.SESSION_CREATE, State.SESSION_EDIT -> {
                stateTransition(State.ADVENTURE, adventureFragment)
            }
            State.PLAYER_DETAILS -> {
                stateTransition(State.PLAYERS, playersFragment)
            }
            else -> {
                stateTransition(State.ADVENTURE, adventureFragment)
            }
        }
    }


    override fun onDetach() {
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AdventureTabsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                AdventureTabsFragment()
    }

    override fun onSessionSelected(session: Session) {
        selectedSession = session
        stateTransition(State.SESSION, SessionFragment.newInstance(session))
    }

    override fun onPlayersFragmentInteraction(item: Player?){
        stateTransition(State.PLAYER_DETAILS, PlayerDetailsFragment.newInstance(item))
    }

    enum class State {
        ADVENTURE,
        ADV_EDIT,
        PLAYERS,
        PLAYER_DETAILS,
        SESSION,
        SESSION_CREATE,
        SESSION_EDIT
    }
}
