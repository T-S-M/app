package com.tsm.way.ui.Feed;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsm.way.R;
import com.tsm.way.firebase.FirebaseDBHelper;
import com.tsm.way.models.Plan;
import com.tsm.way.ui.plan.fragments.AboutPlanFragment;
import com.tsm.way.ui.plan.fragments.ConfirmedGuestListFragment;
import com.tsm.way.ui.plan.fragments.PlanDiscussionFragment;
import com.tsm.way.ui.plan.fragments.TaskFragment;

public class PublicPlanDetailsActivity extends AppCompatActivity {


    private final Fragment discussion = new PlanDiscussionFragment();
    private ImageView coverImageView;
    private Plan plan;
    private Bundle planBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_plan_details);
        supportPostponeEnterTransition();
        coverImageView = findViewById(R.id.plan_cover_image);
        Intent intentThatStartedThisActivity = getIntent();
        handleIntentExtras(intentThatStartedThisActivity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            coverImageView.setTransitionName(plan.getDiscussionID());
        }
        showImageWithTransition(plan.getCoverUrl());
        prepareBundles(plan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PublicPlanDetailsActivity.SectionsPagerAdapter mSectionsPagerAdapter = new PublicPlanDetailsActivity.SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.viewpager_plan_detail);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Button joinButton = findViewById(R.id.join_button);

    }

    private void handleIntentExtras(Intent intentThatStartedThisActivity) {
        if (intentThatStartedThisActivity.hasExtra("plan")) {
            plan = intentThatStartedThisActivity.getParcelableExtra("plan");
        } else if (intentThatStartedThisActivity.hasExtra("id_tag")) {
            String id = intentThatStartedThisActivity.getStringExtra("id_tag");
            DatabaseReference ref = FirebaseDBHelper.getFirebaseDatabaseInstance().getReference("plans").child(id);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    plan = dataSnapshot.getValue(Plan.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(PublicPlanDetailsActivity.this, "There is an Error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void prepareBundles(Plan plan) {
        planBundle = new Bundle();
        planBundle.putParcelable("plan", plan);
        Bundle discussionBundle = new Bundle();
        discussionBundle.putString("id", plan.getDiscussionID());
        discussion.setArguments(discussionBundle);
    }

    private void showImageWithTransition(String coverUrl) {

        if (coverUrl == null || coverUrl.equals("")) {
            supportStartPostponedEnterTransition();
            return;
        }

        Glide.with(this)
                .load(coverUrl)
                .apply(new RequestOptions())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(coverImageView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_plan) {
            Toast.makeText(PublicPlanDetailsActivity.this, "You do not have permission to delete", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Fragment detail = new AboutPlanFragment();
                    detail.setArguments(planBundle);
                    return detail;
                case 1:
                    return discussion;
                case 3:
                    Fragment guest = new ConfirmedGuestListFragment();
                    guest.setArguments(planBundle);
                    return guest;
                case 2:
                    Fragment task = new TaskFragment();
                    task.setArguments(planBundle);
                    return task;
            }
            return null;

        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "About";
            }
            return null;
        }
    }

}
