package com.tsm.way.ui.plan;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.tsm.way.R;
import com.tsm.way.firebase.FirebaseDBHelper;
import com.tsm.way.model.Guest;
import com.tsm.way.model.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFragment extends Fragment {

    Plan mPlan;
    FirebaseListAdapter mAdapter;

    public InviteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        Bundle args = getArguments();
        mPlan = args.getParcelable("plan");
        String id = mPlan.getDiscussionID();

        final DatabaseReference userPendingRef = FirebaseDBHelper.getFirebaseDatabaseInstance().getReference().child("pending");
        final Map guestlist = new HashMap<String, Boolean>();
        guestlist.put(id, true);
        final Map pushtouserMap = new HashMap<String, Map>();
        final TextView test = (TextView) view.findViewById(R.id.selected);
        Button button = (Button) view.findViewById(R.id.invite_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPendingRef.updateChildren(pushtouserMap);
                Snackbar.make(v, "Invited!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
        ListView personListView = (ListView) view.findViewById(R.id.guest_list);
        personListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        DatabaseReference ref = FirebaseDBHelper.getFirebaseDatabaseInstance().getReference().child("users");
        mAdapter = new FirebaseListAdapter<Guest>(getContext(), Guest.class, R.layout.list_item_guest, ref) {
            @Override
            protected void populateView(View view, Guest person, int position) {
                String info = person.getDisplayName() + "\n" + person.getEmail();
                ((CheckedTextView) view.findViewById(R.id.person_name)).setText(info);

            }
        };
        personListView.setAdapter(mAdapter);
        personListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Guest g = (Guest) mAdapter.getItem(position);
                if (pushtouserMap.containsKey(g.getUid())) {
                    pushtouserMap.remove(g.getUid());
                    test.setText("removed " + g.getEmail());
                } else {
                    pushtouserMap.put(g.getUid(), guestlist);
                    test.setText("added " + g.getEmail());
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

}
