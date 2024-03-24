package com.example.womensafety.Activities.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.womensafety.Adapter.LocationAdapter;
import com.example.womensafety.Model.LocationModel;
import com.example.womensafety.R;
import com.example.womensafety.WrapContentLinearLayoutManager;
import com.example.womensafety.databinding.FragmentDashboardBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseDatabase database ;
    private FirebaseAuth auth ;
    private LocationAdapter adapter ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Loading");
        pd.setMessage("Please Wait...");
        pd.show();


        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(requireContext(),MapsActivity.class));
            }
        });

        binding.recView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        Query query = database.getReference().child("SafePlaces").child(auth.getCurrentUser().getUid());
        FirebaseRecyclerOptions<LocationModel> options =new FirebaseRecyclerOptions.Builder<LocationModel>().setQuery(query,LocationModel.class).build();
        adapter =new LocationAdapter(options){
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pd.dismiss();
            }
            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Toast.makeText(getContext(), "Failed to get the locations", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        };
        binding.recView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}