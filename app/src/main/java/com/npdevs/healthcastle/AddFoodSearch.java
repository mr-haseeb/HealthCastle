package com.npdevs.healthcastle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddFoodSearch extends AppCompatActivity {
	private EditText food;
	private RecyclerView recyclerView;
	private String SEARCH;
	List<SampleItem> msampleItem = new ArrayList<>();
	private DatabaseHelper databaseHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_food_search);
		food = findViewById(R.id.editText11);
		recyclerView = findViewById(R.id.recyclerview);
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddFoodSearch.this);
		recyclerView.setLayoutManager(layoutManager);
		RecyclerView.Adapter adapter = new MainAdapter(msampleItem);
		recyclerView.setAdapter(adapter);
		databaseHelper = new DatabaseHelper(this);
		Cursor res = databaseHelper.getAllData();
		while (res.moveToNext()){
			msampleItem.add(new SampleItem(res.getString(1)));
		}
		food.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				// Toast.makeText(getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
				msampleItem.clear();
				Cursor res = databaseHelper.getAllData();
				while (res.moveToNext()){
					if(res.getString(1).toLowerCase().indexOf(charSequence.toString().toLowerCase())>=0){
						msampleItem.add(new SampleItem(res.getString(1)));
					}
				}
				//msampleItem.add(new SampleItem("new"));
				recyclerView.setHasFixedSize(true);
				RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddFoodSearch.this);
				recyclerView.setLayoutManager(layoutManager);
				RecyclerView.Adapter adapter = new MainAdapter(msampleItem);
				recyclerView.setAdapter(adapter);
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		try {
			SEARCH = getIntent().getStringExtra("SEARCH");
			food.setText(SEARCH);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

		private List<SampleItem> samples;

		class ViewHolder extends RecyclerView.ViewHolder {

			private TextView textView;

			ViewHolder(View view) {
				super(view);
				textView = view.findViewById(R.id.textView11);
			}
		}

		MainAdapter(List<SampleItem> samples) {
			this.samples = samples;
			Log.e("nsp",samples.size()+"");
		}

		@NonNull
		@Override
		public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = LayoutInflater
					.from(parent.getContext())
					.inflate(R.layout.item_main_feature, parent, false);

			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
			holder.textView.setText(samples.get(position).getFood());
			final String str = holder.textView.getText().toString();
			holder.textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(AddFoodSearch.this,AddFood.class);
					intent.putExtra("Food",str);
					startActivity(intent);
					finish();
				}
			});
		}

		@Override
		public int getItemCount() {
			return samples.size();
		}
	}
}