package com.example.bookapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.databinding.ActivityAddCategoryBinding
import com.example.bookapp.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.HolderCategory> {
    private val context: Context
    private val categoryArrayList: ArrayList<ModelCategory>

    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>){
        this.context = context
        this.categoryArrayList = categoryArrayList
    }
    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){
        //init ui views
        var categoryTv: TextView = binding.categoryTv
        var delButton: ImageButton = binding.deleteBtn
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HolderCategory {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),
            parent, false)
        return HolderCategory(binding.root)

    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        //get data
        val model = categoryArrayList[position]
        val id = model.id
        val uid = model.uid
        val category = model.category
        val timestamp = model.timestamp

        holder.categoryTv.text = category
        holder.delButton.setOnClickListener {
            var builder = AlertDialog.Builder(context)
            builder.setTitle("delete...")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }
    }

    private fun deleteCategory(model: ModelCategory, holder: CategoryAdapter.HolderCategory) {
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context, "Error, ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }
}