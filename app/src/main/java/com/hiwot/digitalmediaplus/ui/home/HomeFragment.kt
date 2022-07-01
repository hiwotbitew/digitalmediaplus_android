package com.hiwot.digitalmediaplus.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hiwot.digitalmediaplus.api.APIUtil
import com.hiwot.digitalmediaplus.api.Service
import com.hiwot.digitalmediaplus.databinding.FragmentHomeBinding
import com.hiwot.digitalmediaplus.databinding.ThreadNewsFeedBinding
import com.hiwot.digitalmediaplus.databinding.ThreadTxRowBinding
import com.hiwot.digitalmediaplus.db.Persist
import com.hiwot.digitalmediaplus.model.News
import com.hiwot.digitalmediaplus.model.Transaction
import com.hiwot.digitalmediaplus.util.Constants
import okhttp3.Call
import okhttp3.Response
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.IOException
import javax.security.auth.callback.Callback

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var userId: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val pref= Persist(requireContext())
        val name=pref.load(Constants.KEY_FULL_NAME)
        val email=pref.load(Constants.KEY_EMAIL)
        val avatar=pref.loadBitmap(Constants.KEY_AVATAR)
        userId=pref.load(Constants.KEY_USER_ID)!!
        val balance=pref.load(Constants.KEY_BALANCE)
        binding.tvBalance.text=balance

        loadRecentTransactions();
        loadNews();
        return root
    }

    private fun loadNews() {
        val map=HashMap<String,String>()
        map.put("user_id",userId)
        APIUtil.post(context,map,Service.apiNewsShort).enqueue(object: okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("hiwot","error connecting",e)
            }

            override fun onResponse(call: Call, response: Response) {
                try{
                    val body=response.body?.string()
                    Log.e("hiwot","resp: $body")
                    val root=JSONParser().parse(body) as JSONObject
                    if("success"==root["status"]){
                        val data=root["data"] as JSONArray
                        val newsList=ArrayList<News>()
                        for (news in data){
                            val n=News(
                                (news as JSONObject)["title"].toString(),
                                news["time"].toString()
                            )
                            newsList.add(n)
                        }
                        applyNews(newsList)
                    }else{
                        Log.e("hiwot","false returned")
                    }
                }catch (ex:Exception){
                    Log.e("hiwot","error",ex);
                }
            }

        })
    }

    private fun applyNews(newsList:ArrayList<News>) {
        requireActivity().runOnUiThread {
            if(view!=null) {
                for (news in newsList) {
                    val b = ThreadNewsFeedBinding.inflate(LayoutInflater.from(context))
                    b.tvContent.text = news.title
                    b.tvTime.text = news.time
                    binding.feedParent.addView(b.root)
                }
            }
        }
    }

    private fun loadRecentTransactions() {
        val map=HashMap<String,String>()
        map.put("user_id","4")
        APIUtil.post(context,map, Service.apiTxListShort).enqueue(object: Callback,
            okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("hiwot","error loading txlist",e)
            }

            override fun onResponse(call: Call, response: Response) {
                try{
                    val body=response.body?.string()
                    Log.e("hiwot","txlist: $body")
                    val parser=JSONParser()
                    val root:JSONObject=parser.parse(body) as JSONObject
                    if("success"==root["status"]){
                        val data=root["data"] as JSONArray
                        val list=ArrayList<Transaction>()
                        for(key in data){
                            val tx=Transaction(
                                (key as JSONObject)["type"].toString(),
                                key["amount"].toString(),
                                key["date"].toString()
                            );
                            list.add(tx)
                        }
                        applyList(list);
                    }else{
                        Log.e("hiwot","error response");
                    }
                }catch (exp:Exception){
                    Log.e("hiwot","error loading txlist",exp)
                }
            }

        })
    }

    private fun applyList(list: ArrayList<Transaction>) {
        requireActivity().runOnUiThread {
            if(view!=null){
          for(tx in list){
              val b=ThreadTxRowBinding.inflate(LayoutInflater.from(context))
              b.tvType.text=tx.type
              b.tvAmount.text=tx.amount
              b.tvDate.text=tx.date
              binding.tableTransactions.addView(b.root)
              }
          }
        };
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}