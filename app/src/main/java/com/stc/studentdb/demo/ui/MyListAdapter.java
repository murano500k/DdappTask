package com.stc.studentdb.demo.ui;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stc.studentdb.demo.R;
import com.stc.studentdb.demo.data.json.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by artem on 9/28/16.
 */

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {
	private final ListClickListener listener;
	private List<Student> students;



	@Override
	public long getItemId(int position) {
		return students.get(position).getId().hashCode();
	}

	public MyListAdapter(ListClickListener l) {
		this.students = new ArrayList<>();
		this.listener=l;
	}
	public void addItems(List<Student>update){
		this.students.addAll(update);
	}
	public void clearItems(){
		this.students.clear();
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.student_list_item, parent, false);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		Student student = students.get(position);
		holder.item =student;

		holder.name.setText(student.getFirstName());
		holder.lastName.setText(student.getLastName());
		holder.birthday.setText(getDate(student.getBirthday()));
		holder.button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.ListClicked(student);
			}
		});
	}

	@Override
	public int getItemCount() {
		return students.size();
	}


	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView name;
		public TextView lastName;
		public TextView birthday;
		public ImageView button;
		public View view;
		public Student item;

		public MyViewHolder(View view) {
			super(view);
			this.view=view;
			name = (TextView) view.findViewById(R.id.name);
			lastName = (TextView) view.findViewById(R.id.lastname);
			birthday = (TextView) view.findViewById(R.id.birthday);
			button = (ImageView) view.findViewById(R.id.button);

		}
	}




	public interface ListClickListener{
		void ListClicked(Student s);
	}
	private String getDate(long time) {
		return DateFormat.format("dd-MMM-yyyy", new Date(time*1000)).toString();
	}
}
