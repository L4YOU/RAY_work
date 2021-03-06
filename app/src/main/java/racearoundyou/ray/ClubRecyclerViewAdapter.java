package racearoundyou.ray;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import racearoundyou.ray.ClubSearch.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder> {

    private List<Club> mValues;
    private OnListFragmentInteractionListener mListener;
    private List<Club> mValuesCopy;

    public ClubRecyclerViewAdapter(List<Club>ClubList, OnListFragmentInteractionListener listener) {
        mValues = ClubList;
        mListener = listener;
        mValuesCopy = new ArrayList<>();
        mValuesCopy.addAll(mValues);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int postiton) {
        holder.mItem = mValues.get(postiton);
        holder.mNameView.setText(mValues.get(postiton).getName());
        holder.mDescView.setText(mValues.get(postiton).getDescription());
        holder.mAmountView.setText(String.valueOf(mValues.get(postiton).getUserAmount()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void filter(String text) {
        mValues.clear();
        if(text.isEmpty()){
            mValues.addAll(mValuesCopy);
        } else{
            text = text.toLowerCase();
            for(Club item: mValuesCopy){
                if(item.getName().toLowerCase().contains(text)){
                    mValues.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDescView;
        public final TextView mAmountView;
        public Club mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.clubname);
            mDescView = (TextView) view.findViewById(R.id.clubdesc);
            mAmountView = (TextView) view.findViewById(R.id.clubamount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescView.getText() + "'";
        }
    }
    public Club getItem(int position){
        return mValues.get(position);
    }
}
