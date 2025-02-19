package com.example.huntlow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView textUsername;
    private BarChart barChart;
    private List<BarEntry> entries;
    private List<String> labels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textUsername = view.findViewById(R.id.text_username);
        barChart = view.findViewById(R.id.bar_chart);

        String currentUsername = ((MainActivity) getActivity()).getCurrentUsername();
        textUsername.setText(currentUsername);

        fetchAppOpenData();

        return view;
    }

    public void onOrientationChanged() {
        setupChart();
    }

    private void fetchAppOpenData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("appOpens").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entries = new ArrayList<>();
                labels = new ArrayList<>();
                int index = 0;

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    long count = (long) dateSnapshot.getValue();
                    entries.add(new BarEntry(index, count));
                    labels.add(date);
                    index++;
                }

                setupChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void setupChart() {
        if (entries == null || labels == null) return;

        BarDataSet dataSet = new BarDataSet(entries, "Nombre d'ouvertures");
        dataSet.setColor(Color.BLUE);
        BarData barData = new BarData(dataSet);
        barData.setValueTextColor(Color.BLACK);
        barData.setValueTextSize(12f);

        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setTextColor(Color.BLACK);
        barChart.getLegend().setTextSize(12f);
        barChart.setBackgroundColor(Color.WHITE);

        barChart.invalidate(); // Refresh the chart
    }
}
