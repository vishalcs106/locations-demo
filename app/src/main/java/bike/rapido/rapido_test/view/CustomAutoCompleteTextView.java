package bike.rapido.rapido_test.view;

/**
 * Created by Dell 3450 on 7/12/2017.
 */



import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;


public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }
}
