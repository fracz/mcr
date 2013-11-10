package pl.fracz.mcr.fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.SherlockListFragment;
import com.googlecode.androidannotations.annotations.EFragment;
import pl.fracz.mcr.R;

@EFragment(R.layout.comments_preview)
public class CommentsPreview extends SherlockListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                new String[]{"PIerwszy", "Drugi"}));
    }
}
