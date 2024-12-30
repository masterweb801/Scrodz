package com.logicrealm.scordz.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicrealm.scordz.R
import com.logicrealm.scordz.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar


data class RowData(
    var team1Score: String,
    var team2Score: String
)

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RowAdapter
    private lateinit var tot1: TextView
    private lateinit var tot2: TextView
    private lateinit var addBtn: Button
    private var team1Total = 0
    private var team2Total = 0
    private var team1Name = "Team 1"
    private var team2Name = "Team 2"
    private lateinit var clearBtn: Button
    private lateinit var saveBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.tableRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RowAdapter { updateTotals() }
        recyclerView.adapter = adapter

        addBtn = binding.addRowButton
        addBtn.setOnClickListener {
            val success = adapter.addRow()
            if (!success) {
                Snackbar.make(binding.root, "Please fill in the previous row before adding a new one.", Snackbar.LENGTH_SHORT).show()
            }
        }

        clearBtn = binding.clearButton
        clearBtn.setOnClickListener {
            adapter.clearRows()
            Snackbar.make(binding.root, "Rounds Cleared", Snackbar.LENGTH_SHORT).show()
            updateTotals()
        }

        // TODO: Save Functionality
        saveBtn = binding.saveButton

        saveBtn.setOnClickListener {
            Snackbar.make(binding.root, "This feature will be implemented in future", Snackbar.LENGTH_SHORT).show()
        }

        updateTotals()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotals() {
        team1Total = adapter.rows.sumOf { it.team1Score.toIntOrNull() ?: 0 }
        team2Total = adapter.rows.sumOf { it.team2Score.toIntOrNull() ?: 0 }

        tot1 = binding.team1Total
        tot2 = binding.team2Total

        tot1.text = "$team1Name: $team1Total"
        tot2.text = "$team2Name: $team2Total"
    }
}

class RowAdapter(private val onRowUpdated: () -> Unit) :
    RecyclerView.Adapter<RowAdapter.RowViewHolder>() {

    val rows = mutableListOf<RowData>()

    inner class RowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val roundText: TextView = view.findViewById(R.id.roundText)
        val team1Input: EditText = view.findViewById(R.id.team1Input)
        val team2Input: EditText = view.findViewById(R.id.team2Input)
        private val deleteButton: Button = view.findViewById(R.id.deleteButton)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int, rowData: RowData) {
            roundText.text = (position + 1).toString()

            team1Input.setText(rowData.team1Score)
            team2Input.setText(rowData.team2Score)

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    rowData.team1Score = team1Input.text.toString()
                    rowData.team2Score = team2Input.text.toString()
                    onRowUpdated()
                }
            }

            team1Input.addTextChangedListener(watcher)
            team2Input.addTextChangedListener(watcher)

            deleteButton.setOnClickListener {
                rows.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, rows.size)
                onRowUpdated()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return RowViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(position, rows[position])
    }

    override fun getItemCount(): Int = rows.size

    fun addRow(score1: String = "", score2: String = ""): Boolean {
        if (rows.isNotEmpty()) {
            val lastRow = rows.last()
            if (lastRow.team1Score.isBlank() || lastRow.team2Score.isBlank()) {
                return false
            }
        }

        rows.add(RowData(score1, score2))
        notifyItemInserted(rows.size - 1)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearRows() {
        rows.clear()
        notifyDataSetChanged()
    }

}