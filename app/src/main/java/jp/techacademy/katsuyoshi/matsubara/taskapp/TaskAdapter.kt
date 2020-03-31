package jp.techacademy.katsuyoshi.matsubara.taskapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


//それぞれのメソッドの中身を実装する前に、アイテムを保持する List を
// taskList というプロパティで定義します。クラスはタスクを表す Task クラスに
// 後ほど修正しますが、まずは String クラスとしておきます。
// これは、Taskクラスを実装するまえに、ListViewの動きを確認できるようにするためです。Task クラスは後のRealmのチャプターで実装します
class TaskAdapter(context: Context): BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var taskList = mutableListOf<Task>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return taskList.size
    }

    override fun getItem(position: Int): Any {
        return taskList[position]
    }

    override fun getItemId(position: Int): Long {
        return taskList[position].id.toLong()
    }

    //次にgetViewメソッドを実装します。そのために、他のxmlリソースのViewを
    // 取り扱うための仕組みであるLayoutInflaterをプロパティとして定義しておき
    // 、コンストラクタを新規に追加して取得しておきます。
    // そしてgetViewメソッドの引数であるconvertViewがnullのときは
    // LayoutInflaterを使ってsimple_list_item_2からViewを取得します
    // (エルビス演算子?:は左辺がnullの時に右辺を返します)。
    // simple_list_item_2はタイトルとサブタイトルがあるセルです。まずは
    // String型で保持しているtaskListから文字列を取得しタイトルを設定するように
    // 実装しておきます。後でそれぞれタイトルとサブタイトルにタスクの情報を
    // 設定するように修正します。
    // なお、getViewメソッドの引数であるconvertView、すなわち現在表示しようと
    // している行がnullかどうか判定を行っているのは、BaseAdapterにViewを
    // 再利用して描画する仕組みがあるためです。
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null)

        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        val textView2 = view.findViewById<TextView>(android.R.id.text2)
        //後でTaskクラスから情報を取得するように変更する
        textView1.text = taskList[position].title

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE)
        val date = taskList[position].date
        textView2.text = simpleDateFormat.format(date)

        return view
    }

}