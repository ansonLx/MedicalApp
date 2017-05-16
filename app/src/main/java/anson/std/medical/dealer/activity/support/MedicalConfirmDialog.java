package anson.std.medical.dealer.activity.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.R;

/**
 * Created by anson on 17-5-16.
 */

public class MedicalConfirmDialog<T> {

    private Context context;

    public MedicalConfirmDialog(Context context) {
        this.context = context;
    }

    public void openConfirmDialog(String message, final T t, final Consumer<T> callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.confirm_sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(callback != null){
                    callback.apply(t);
                }
            }
        });
        builder.setNegativeButton(R.string.confirm_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
