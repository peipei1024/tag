package com.ngc123.tag.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.ngc123.tag.R;


public class BaseFragment extends Fragment {
  protected Context ctx;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ctx = getActivity();
  }

  protected void toast(String str) {
    Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
  }

  protected void toast(int id) {
    Toast.makeText(this.getActivity(), id, Toast.LENGTH_SHORT).show();
  }

  protected boolean filterException(Exception e) {
    if (e != null) {
      toast(e.getMessage());
      return false;
    } else {
      return true;
    }
  }

  protected ProgressDialog showSpinnerDialog() {
    //activity = modifyDialogContext(activity);
    ProgressDialog dialog = new ProgressDialog(getActivity());
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setCancelable(true);
    dialog.setMessage(getString(R.string.chat_utils_hardLoading));
    if (!getActivity().isFinishing()) {
      dialog.show();
    }
    return dialog;
  }
}
