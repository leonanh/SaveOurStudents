package com.sos.saveourstudents;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deamon on 4/21/15.
 */
public class FragmentFeed extends Fragment {

    private RecycleViewAdapter mAdapter;

    static CardManager mCardManagerInstance;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    Context mContext;
    //private String[] myDataset = {"1", "2", "1", "2", "1", "2"};

    static List<Question> mQuestionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.feed_layout, container,
                false);


        //TODO Move into server call (Async or volley)
        mQuestionList = new ArrayList<Question>();
        for(int a = 0;a<5;a++){
            Question temp = new Question("Question "+a);
            mQuestionList.add(temp);
        }


        mContext = this.getActivity().getBaseContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        //mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewAdapter(CardManager.getInstance().getCounters(), R.layout.feed_item_layout, mContext);
        mRecyclerView.setAdapter(mAdapter);








        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        /*
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewAdapter(CardManager.getInstance().getCounters(), R.layout.simple_nav_drawer_item, mContext);
        mRecyclerView.setAdapter(mAdapter);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        */

        return rootView;
    }

    public static class CardManager {

        public static CardManager getInstance() {
            if (mCardManagerInstance == null) {
                mCardManagerInstance = new CardManager();
            }

            return mCardManagerInstance;
        }

        public List<Question> getCounters() {
            if (mQuestionList == null) {
                mQuestionList = new ArrayList<Question>();

            }
            return mQuestionList;
        }
    }

    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

        private List<Question> questions;
        private int rowLayout;

        public RecycleViewAdapter(List<Question> questions, int rowLayout, Context context) {
            this.questions = questions;
            this.rowLayout = rowLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.questionText.setText(mCardManagerInstance.getCounters().get(i).title+"");
            //viewHolder.venueType.setText(mInstance.getCounters().get(i)+"");

			/*
			Counter counter = counters.get(i);
			viewHolder.counterName.setText(counter.name);
			viewHolder.counterIncrement.setText(counter.increment+"");
			viewHolder.counterTotal.setText(counter.total+"");

			int color = CounterManager.getInstance().getCounters().get(i).color;

			viewHolder.rippleView.setRippleColor(color);
			viewHolder.counterName.setTextColor(color);
			viewHolder.counterIncrement.setTextColor(color);
			viewHolder.counterTotal.setTextColor(color);

			viewHolder.upArrow.setColorFilter(color);
			viewHolder.downArrow.setColorFilter(color);
			*/

        }

        @Override
        public int getItemCount() {
            return questions == null ? 0 : questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

            public TextView questionText;
            private RippleView rippleView;


            public ViewHolder(View itemView) {
                super(itemView);
                questionText = (TextView) itemView.findViewById(R.id.feed_text);
                rippleView = (RippleView) itemView.findViewById(R.id.more);
                //rippleView.setRippleColor(getResources().getColor(R.color.blue));

                //rippleView.setOnTouchListener(this);

				/*
				counterName = (TextView) itemView.findViewById(R.id.counter_name);
				counterIncrement = (TextView) itemView.findViewById(R.id.counter_increment);
				counterTotal = (TextView) itemView.findViewById(R.id.counter_total);
				cardView = (CardView) itemView.findViewById(R.id.cardview);


				upArrow = (ImageView) itemView.findViewById(R.id.up_image_button);
				downArrow = (ImageView) itemView.findViewById(R.id.down_image_button);

				upArrow.setOnClickListener(this);
				downArrow.setOnClickListener(this);


				*/
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }

        }

    }
}