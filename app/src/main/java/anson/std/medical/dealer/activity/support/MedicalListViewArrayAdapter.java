package anson.std.medical.dealer.activity.support;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.R;

/**
 * Created by xq on 2017/5/14.
 */

public class MedicalListViewArrayAdapter<T> extends ArrayAdapter {

    private int resource;
    private Context context;
    private List<DataOnItem> dataList;
    private Method getShowNameMethod;
    private int checkedColor;
    private Consumer<T> editCallback;

    public MedicalListViewArrayAdapter(Context context, List<T> dataList, Method getShowNameMethod, Consumer<T> editCallback) {
        super(context, R.layout.array_list_view_layout);
        this.resource = R.layout.array_list_view_layout;
        this.context = context;
        this.dataList = new ArrayList<>();
        for (T data : dataList) {
            DataOnItem dataOnItem = new DataOnItem();
            dataOnItem.data = data;
            this.dataList.add(dataOnItem);
        }
        this.getShowNameMethod = getShowNameMethod;
        this.checkedColor = context.getResources().getColor(R.color.colorAccent, null);
        this.editCallback = editCallback;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DataOnItem itemData = dataList.get(position);
        itemData.position = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
        }
        TextView nameView = (TextView) convertView.findViewById(R.id.contact_name_view);
        try {
            nameView.setText((String) getShowNameMethod.invoke(itemData.data));
            itemData.nameView = convertView;
            if (itemData.isCheck) {
                convertView.setBackgroundColor(checkedColor);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Button button = (Button) convertView.findViewById(R.id.contact_edit_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCallback.apply(itemData.data);
            }
        });
        convertView.setTag(itemData);

        return convertView;
    }

    public String onItemClick(View itemView) {
        String checkedName = "";
        try {
            DataOnItem itemData = (DataOnItem) itemView.getTag();

            for (DataOnItem data : dataList) {
                if (data.position == itemData.position) {
                    itemData.isCheck = !itemData.isCheck;
                } else {
                    data.isCheck = false;
                }
                if (data.nameView != null) {
                    data.nameView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            if (itemData.isCheck) {
                itemView.setBackgroundColor(checkedColor);
                checkedName = (String) getShowNameMethod.invoke(itemData.data);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return checkedName;
    }

    public T getSelectedItem(){
        for (DataOnItem data : dataList) {
            if(data.isCheck){
                return data.data;
            }
        }
        return null;
    }

    private class DataOnItem {
        private int position;
        private T data;
        private boolean isCheck;
        private View nameView;
    }
}
