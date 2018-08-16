package sheva.singapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sheva.singapp.R;

public class LoginFragment extends DialogFragment {
    private static final String USERNAME = "param1";
    @BindView(R.id.etInfoLoginPassword)
    EditText etPassword;
    @BindView(R.id.etInfoLoginProfileID)
    EditText etProfileID;
    @BindView(R.id.btnInfoLoginCancel)
    Button btnCancel;
    @BindView(R.id.btnInfoLoginLogin)
    Button btnLogin;
    private Unbinder unbinder;

    private String username;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_info_login, container, false);
        unbinder = ButterKnife.bind(this, v);
        if (!username.equals("") ||
                username != null) {
            etProfileID.setText(username);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String profileID = etProfileID.getText().toString();
                String password = etPassword.getText().toString();
                onLogin(profileID, password);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.closeFragment(LoginFragment.this);
            }
        });
        return v;
    }

    public void onLogin(String loginUsername, String password) {
        if (mListener != null) {
            mListener.onLogIn(loginUsername, password);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public interface OnFragmentInteractionListener {
        void onLogIn(String username, String password);
        void closeFragment(Fragment fragment);
    }
}
