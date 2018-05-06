package com.example.mark.racetheworld.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mark.racetheworld.R;
import com.example.mark.racetheworld.Utilities.ImageHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageView image = view.findViewById((R.id.profile_image));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageHelper helper = new ImageHelper();

        helper.setImageFromUrl(image, user.getPhotoUrl().toString());

        return view;
    }
}
