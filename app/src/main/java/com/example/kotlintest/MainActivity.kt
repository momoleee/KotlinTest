package com.example.kotlintest

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintest.data.ItemDetailResponse
import com.example.kotlintest.data.ItemListResponse
import com.example.kotlintest.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var mAdapter : MainAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    val linearLayoutManager = LinearLayoutManager(this)

    var startPosition : Int = 0
    var searchingCategory : Int? = null
    var searchingKeyword : String = ""
    val count : Int = 10
    var isLoading = false

    private lateinit var bottomSheetView : View
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<View>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get data without any condition
        loadData(searchingKeyword)
        // set search bar editText listener
        setEditTextListener()

        // create and set bottomSheetDialog
        createBottomSheetDialog()

        // open bottomSheetDialog when iv_main_filter clicked
        iv_main_filter.setOnClickListener(View.OnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetDialog.show()
        })
    }



    fun createBottomSheetDialog(){
        bottomSheetView = layoutInflater.inflate(R.layout.bottomsheet_layout, null)
        bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheetDialog.dismiss()
            }
        })


        bottomSheetView.findViewById<View>(R.id.category_0)
            .setOnClickListener {
                Toast.makeText(this@MainActivity, "스킨/토너", Toast.LENGTH_SHORT).show()
                tv_sub_category_selected.setText("스킨/토너")
                searchingCategory = 0
                reloadData()
                bottomSheetDialog.dismiss()
            }
        bottomSheetView.findViewById<View>(R.id.category_10)
            .setOnClickListener {
                Toast.makeText(this@MainActivity, "클렌징크림", Toast.LENGTH_SHORT).show()
                tv_sub_category_selected.setText("클렌징크림")
                searchingCategory = 10
                reloadData()
                bottomSheetDialog.dismiss()
            }
        bottomSheetView.findViewById<View>(R.id.category_20)
            .setOnClickListener {
                Toast.makeText(this@MainActivity, "선블록", Toast.LENGTH_SHORT).show()
                tv_sub_category_selected.setText("선블록")
                searchingCategory = 20
                reloadData()
                bottomSheetDialog.dismiss()
            }
        bottomSheetView.findViewById<View>(R.id.category_25)
            .setOnClickListener {
                Toast.makeText(this@MainActivity, "마스크시트", Toast.LENGTH_SHORT).show()
                tv_sub_category_selected.setText("마스크시트")
                searchingCategory = 25
                reloadData()
                bottomSheetDialog.dismiss()
            }

        bottomSheetView.findViewById<View>(R.id.tv_btn_reset)
            .setOnClickListener {
                Toast.makeText(this@MainActivity, "초기화", Toast.LENGTH_SHORT).show()
                tv_sub_category_selected.setText("")
                searchingCategory = null
                reloadData()
                bottomSheetDialog.dismiss()
            }

    }



    fun loadData(keyword: String){
        main_progressbar.visibility = View.VISIBLE

        // set request body
        val json = JSONObject()
        if(keyword != "") json.put("keyword", keyword)
        json.put("count", count)
        json.put("start", startPosition)

        if(searchingCategory != null){
            val categoryJsonArray = JSONArray()
            categoryJsonArray.put(searchingCategory!!)
            json.put("sub_category_ids", categoryJsonArray)
        }

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        // create RetrofitClient
        RetrofitClient.retrofitService.getCosmetics(requestBody).enqueue(object : Callback<ItemListResponse> {
            override fun onFailure(call: Call<ItemListResponse>, t: Throwable) {
                main_progressbar.visibility = View.GONE
                Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ItemListResponse>,
                response: Response<ItemListResponse>
            ) {
                main_progressbar.visibility = View.GONE
                if(response.code() == 200){
                    val body = response.body()

                    if (body != null && body.items.size != 0) {
                        if (startPosition == 0){
                            tv_msg_no_data.visibility = View.GONE
                            rv_main_list.visibility = View.VISIBLE
                            setAdapter(body.items)
                        }else{
                            mAdapter.addItem(body.items)
                            setRecyclerViewScrollListener()
                        }
                        isLoading = false
                        startPosition += count + 1

                    }

                    if (body != null && body.items.size == 0 && startPosition ==0) {
                        tv_msg_no_data.visibility = View.VISIBLE
                        rv_main_list.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun reloadData(){
        startPosition = 0
        searchingKeyword = et_main_search.getText().toString()
        loadData(searchingKeyword)
    }



    private fun setEditTextListener(){
        et_main_search.setOnEditorActionListener{ v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                Toast.makeText(this@MainActivity, et_main_search.getText().toString()+ " 검색", Toast.LENGTH_SHORT).show()
                reloadData()

                // hide keyboard
                val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)

                true
            } else {
                false
            }
        }


        et_main_search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(et_main_search.getText().toString().isEmpty()){
                    tv_sub_category_selected.setText("")
                    searchingCategory = null
                    reloadData()
                }
            }
        })
    }





    private fun setRecyclerViewScrollListener(){
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView?.let { super.onScrolled(it, dx, dy) }
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisible = linearLayoutManager.findFirstVisibleItemPosition()
                if (!isLoading && (visibleItemCount + firstVisible) >= totalItemCount) {
                    isLoading = true
                    // Call your API to load more items
                    rv_main_list.removeOnScrollListener(scrollListener)
                    loadData(searchingKeyword)
                }
            }
        }

        rv_main_list.addOnScrollListener(scrollListener)
    }




    private fun setAdapter(itemList : MutableList<ItemDetailResponse>){
        mAdapter = MainAdapter(itemList,this@MainActivity)
        rv_main_list.adapter = mAdapter
        rv_main_list.layoutManager = linearLayoutManager
        setRecyclerViewScrollListener()
    }
}
