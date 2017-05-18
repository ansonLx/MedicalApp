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
    private int checkedTextColor;
    private Integer orgTextColor;
    private Consumer<T> editCallback;
    private Consumer<T> delCallback;

    public MedicalListViewArrayAdapter(Context context, List<T> dataList, Method getShowNameMethod, Consumer<T> editCallback, Consumer<T> delCallback) {
        super(context, R.layout.array_list_view_layout);
        this.resource = R.layout.array_list_view_layout;
        this.context = context;
        this.dataList = new ArrayList<>();
        if (dataList != null) {
            for (T data : dataList) {
                DataOnItem dataOnItem = new DataOnItem();
                dataOnItem.data = data;
                this.dataList.add(dataOnItem);
            }
        }
        this.getShowNameMethod = getShowNameMethod;
        this.checkedColor = context.getResources().getColor(R.color.list_selected, null);
        this.checkedTextColor = context.getResources().getColor(R.color.list_selected_text, null);
        this.editCallback = editCallback;
        this.delCallback = delCallback;
    }

    public void flushData(List<T> dataList) {
        this.dataList.clear();
        if (dataList != null && !dataList.isEmpty()) {
            for (T data : dataList) {
                DataOnItem dataOnItem = new DataOnItem();
                dataOnItem.data = data;
                this.dataList.add(dataOnItem);
            }
        }
        notifyDataSetChanged();
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
        if (orgTextColor == null) {
            orgTextColor = nameView.getCurrentTextColor();
        }
        try {
            nameView.setText((String) getShowNameMethod.invoke(itemData.data));
            itemData.nameView = convertView;
            if (itemData.isCheck) {
                convertView.setBackgroundColor(checkedColor);
                nameView.setTextColor(checkedTextColor);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
                nameView.setTextColor(orgTextColor);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Button editBtn = (Button) convertView.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editCallback != null) {
                    editCallback.apply(itemData.data);
                }
            }
        });
        final Button delBtn = (Button) convertView.findViewById(R.id.del_btn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delCallback != null) {
                    delCallback.apply(itemData.data);
                }
            }
        });

        convertView.setTag(itemData);

        return convertView;
    }

    public String onItemClick(View itemView) {
        String checkedName = "";
        try {
            TextView nameView = (TextView) itemView.findViewById(R.id.contact_name_view);
            DataOnItem itemData = (DataOnItem) itemView.getTag();

            for (DataOnItem data : dataList) {
                if (data.nameView != null) {
                    if (data.position == itemData.position) {
                        itemData.isCheck = !itemData.isCheck;
                    } else {
                        data.isCheck = false;
                    }
                    data.nameView.setBackgroundColor(Color.TRANSPARENT);
                    TextView name = (TextView) data.nameView.findViewById(R.id.contact_name_view);
                    name.setTextColor(orgTextColor);
                }
            }
            if (itemData.isCheck) {
                itemView.setBackgroundColor(checkedColor);
                nameView.setTextColor(checkedTextColor);
                checkedName = (String) getShowNameMethod.invoke(itemData.data);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return checkedName;
    }

    public T getSelectedItem() {
        for (DataOnItem data : dataList) {
            if (data.isCheck) {
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
