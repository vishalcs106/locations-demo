package in.location_demo.demo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import in.location_demo.demo.R;
import in.location_demo.demo.model.Route;

/**
 * Created by Dell 3450 on 7/13/2017.
 */

public class RoutesListAdapter extends BaseAdapter {
    List<Route> mRoutes;
    Context mContext;
    LayoutInflater inflater;
    public RoutesListAdapter(List<Route> routes, Context context){
        mRoutes = routes;
        mContext = context;
        inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mRoutes.size();
    }

    @Override
    public Object getItem(int i) {
        return mRoutes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        ViewHolder holder;
        if(convertView == null){
            vi = inflater.inflate(R.layout.layout_routes_row, null);
            holder = new ViewHolder();
            holder.sumaryTextView = (TextView) vi.findViewById(R.id.summary_text);
            holder.mDistance = (TextView) vi.findViewById(R.id.distance);
            holder.mDuration = (TextView) vi.findViewById(R.id.duration);
            vi.setTag(holder);
        } else{
            holder = (ViewHolder) vi.getTag();
        }
        holder.sumaryTextView.setText(mRoutes.get(i).SummaryText);
        holder.mDuration.setText(mRoutes.get(i).mDuration);
        holder.mDistance.setText(mRoutes.get(i).mDistance);
        return vi;
    }

    private static class ViewHolder{

        private TextView sumaryTextView;
        private TextView mDuration;
        private TextView mDistance;

    }
}
