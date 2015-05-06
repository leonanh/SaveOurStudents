package com.sos.saveourstudents;


import java.util.ArrayList;
import java.util.List;

import com.andexert.library.RippleView;
import com.sos.saveourstudents.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.util.ArrayList;
import java.util.List;

public class FragmentVenueNavDrawer extends Fragment {
    boolean mDualPane;
    int mCurCheckPosition = 0;

    static private List<Venue> mCounterList;
    static CardManager mInstance;
    private RecycleViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    Context mContext;
    
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        /*
		View rootView = inflater.inflate(R.layout.nav_drawer_layout, container,
				false);

		mCounterList = new ArrayList<Venue>();
		for(int a = 0;a<5;a++){
			Venue temp = new Venue("Venue "+a);
			mCounterList.add(temp);
		}
		
		mContext = this.getActivity().getBaseContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
		mRecyclerView.setHasFixedSize(true);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		mAdapter = new RecycleViewAdapter(CardManager.getInstance().getCounters(), R.layout.simple_nav_drawer_item, mContext);
		mRecyclerView.setAdapter(mAdapter);
		
		*/
		return null; //TODO
	}
    
    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }


    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;
        
        
    }
    
    private class Venue {
    	
    	String type = "Unset Type";
    	String name = "Unset Name";
    	
    	public Venue(String name){
    		this.name = name;
    	}
    }
    
    
    public static class CardManager {
    	
		public static CardManager getInstance() {
			if (mInstance == null) {
				mInstance = new CardManager();
			}

			return mInstance;
		}
		public List<Venue> getCounters() {
			if (mCounterList == null) {
				mCounterList = new ArrayList<Venue>();
				
			}
			return mCounterList;
		}
	}
    
	public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

		private List<Venue> venues;
		private int rowLayout;

		public RecycleViewAdapter(List<Venue> venues, int rowLayout, Context context) {
			this.venues = venues;
			this.rowLayout = rowLayout;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
			return new ViewHolder(v);
		}

		@Override
		public void onBindViewHolder(ViewHolder viewHolder, int i) {
			viewHolder.venueName.setText(mInstance.getCounters().get(i).name+"");
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
			return venues == null ? 0 : venues.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder implements OnTouchListener{
			
			public TextView venueName;
			private RippleView rippleView;
			

			public ViewHolder(View itemView) {
				super(itemView);
				venueName = (TextView) itemView.findViewById(R.id.textview);
				rippleView = (RippleView) itemView.findViewById(R.id.more);
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