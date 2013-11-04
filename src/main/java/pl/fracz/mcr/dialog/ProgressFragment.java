package pl.fracz.mcr.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressFragment extends DialogFragment
{

	private static final String ARGS_MESSAGE = "message";

	public static ProgressFragment newInstance(String message){
		Bundle args = new Bundle();
		args.putString(ARGS_MESSAGE, message);
		ProgressFragment prog = new ProgressFragment();
		prog.setArguments(args);
		prog.setCancelable(false);
		return prog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setTitle("Czekaj");
		progress.setMessage(getArguments().getString(ARGS_MESSAGE));
		return progress;
	}
}
