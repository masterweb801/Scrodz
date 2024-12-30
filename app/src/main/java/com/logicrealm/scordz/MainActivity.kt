package com.logicrealm.scordz

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.logicrealm.scordz.databinding.ActivityMainBinding


data class RowData(
    var team1Score: String,
    var team2Score: String
)

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = ""

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val appVersionTextView: TextView = findViewById(R.id.appVersion)
        val versionName = getAppVersion()
        appVersionTextView.text = "Version $versionName"

        recyclerView = findViewById(R.id.tableRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RowAdapter { updateTotals() }
        recyclerView.adapter = adapter

        addBtn = findViewById(R.id.addRowButton)
        addBtn.setOnClickListener {
            val success = adapter.addRow()
            if (!success) {
                Toast.makeText(this, "Please fill in the previous row before adding a new one.", Toast.LENGTH_SHORT).show()
            }
        }

        clearBtn = findViewById(R.id.clearButton)
        clearBtn.setOnClickListener {
            adapter.clearRows()
            Toast.makeText(this, "Rounds Cleared", Toast.LENGTH_SHORT).show()
            updateTotals()
        }

        // TODO: Save Functionality
        saveBtn = findViewById(R.id.saveButton)

        saveBtn.setOnClickListener {
            Toast.makeText(this, "This feature will be implemented in future", Toast.LENGTH_SHORT).show()
        }

        updateTotals()

    }

    private fun getAppVersion(): String? {
        try {
            val packageManager = packageManager
            val packageName = packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            return "N/A"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotals() {
        team1Total = adapter.rows.sumOf { it.team1Score.toIntOrNull() ?: 0 }
        team2Total = adapter.rows.sumOf { it.team2Score.toIntOrNull() ?: 0 }

        tot1 = findViewById(R.id.team1Total)
        tot2 = findViewById(R.id.team2Total)

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