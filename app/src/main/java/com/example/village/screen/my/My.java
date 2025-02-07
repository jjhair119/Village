package com.example.village.screen.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.village.R;
import com.example.village.rdatabase.LoginDatabase;
import com.example.village.screen.MainActivity;
import com.example.village.screen.login.Login;
import com.example.village.util.LogoutDialog;
import com.example.village.util.SecssionDialog;
import com.example.village.util.SendpasswordDialog;
import com.example.village.util.logoutOncilck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

public class My extends Fragment implements View.OnClickListener, logoutOncilck {
    MainActivity activity;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LogoutDialog dialog;
    SendpasswordDialog dialog1;
    SecssionDialog dialog2;
    private static final String TAG = "MainActivity";

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private String profileUsername;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_my, null);
        dialog = new LogoutDialog(requireActivity(), "로그아웃", "로그아웃 되었습니다.", this);
        dialog1 = new SendpasswordDialog(requireActivity(), "비밀번호 변경", "이메일이 전송되었습니다.", this);
        dialog2 = new SecssionDialog(requireActivity(), "탈퇴완료", "탈퇴 처리가 성공적으로 완료되었습니다.", this);
        String emailAddress = user.getEmail();

        TextView txt_email = v.findViewById(R.id.text_my_useremail);
        txt_email.setText(emailAddress);
        TextView txt_username = v.findViewById(R.id.text_my_username);

        profileUsername = ((MainActivity)MainActivity.nContext).getProfileUsername();
        txt_username.setText(profileUsername);

        Button button_profile = v.findViewById(R.id.button_my_profile);
//        Button button_local = v.findViewById(R.id.button_my_local);
        Button button_password = v.findViewById(R.id.button_my_password);
        Button button_logout = v.findViewById(R.id.button_my_logout);
        Button button_end = v.findViewById(R.id.button_my_end);

        button_profile.setOnClickListener(this);
//        button_local.setOnClickListener(this);
        button_password.setOnClickListener(this);
        button_logout.setOnClickListener(this);
        button_end.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_my_profile:
                activity.onFragmentChange(1);
                break;
//            case R.id.button_my_local:
//                activity.onFragmentChange(3);
//                break;
            case R.id.button_my_password:
                sendPasswordReset();
                break;
            case R.id.button_my_logout:
                logout();
                break;
            case R.id.button_my_end:
                secession();
                break;
        }
    }

    private void logout() {
        dialog.show();
    }

    private void sendPasswordReset() {
        String emailAddress = user.getEmail();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        dialog1.show();
    }

    private void secession() {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
        dialog2.show();
    }

    @Override
    public void onClickdialog() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        LoginDatabase db = Room.databaseBuilder(mContext, LoginDatabase.class
                , "village-login-db")
                .allowMainThreadQueries()
                .build();
        db.LoginDataDao().deleteLogin();
        auth.signOut();
        startActivity(new Intent(requireActivity(), Login.class));
        ActivityCompat.finishAffinity(requireActivity());
        dialog.dismiss();
    }
}