package jp.techacademy.katsuyoshi.matsubara.taskapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

//パッケージ名を含めた文字列をIntentのExtraのキーとして利用するのは、
// 他のアプリのExtraと間違えないようにするためです。今回のアプリでは
// 明示的IntentでActivityを呼び出しているので混同の可能性はありませんが、
// よく使う考え方なので覚えておきましょう。
const val EXTRA_TASK = "jp.techacademy.katsuyoshi.matsubara.taskapp.TASK"

class MainActivity : AppCompatActivity() {
    private lateinit var mRealm: Realm //Realmクラスを保持するmRealmを定義
    //RealmChangeListenerクラスのmRealmListenerはRealmのデータベースに
    // 追加や削除など変化があった場合に呼ばれるリスナーです。
    // onChangeメソッドをオーバーライドしてreloadListViewメソッドを
    // 呼び出すようにします。
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
        }
    }

    private lateinit var mTaskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            startActivity(intent)

            // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //   .setAction("Action", null).show()
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance() //オブジェクトを取得
        mRealm.addChangeListener(mRealmListener) //mRealmListenerをaddChangeListenerメソッドで設定

        //ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        //ListViewをタップした時の処理
        listView1.setOnItemClickListener { parent, _, position, _ ->
            // 入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }
        //長押しした時の処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            //タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            //ダイアログを表示する
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK") { _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }
        // アプリ起動時に表示テスト用のタスクを作成する
        //まだタスク作成画面を実装していないのでonCreateメソッド内で
        // addTaskForTestメソッドを呼び出して addTaskForTestメソッド内で
        // Realmに仮のデータを保存します。

        //addTaskForTest()

        reloadListView()
    }

    //Realmのデータベースに変更があった場合に呼び出されるようにした
    // reloadListViewメソッドの中身も修正.
    // findAll で全てのTaskデータを取得して、sortで"date" を
    // Sort.DESCENDING （降順）で並べ替えた結果を返します。
    // 次にその結果を、 mRealm.copyFromRealm(taskRealmResults)
    // でコピーしてアダプターに渡します。Realmのデータベースから取得した
    // 内容をAdapterなど別の場所で使う場合は直接渡すのではなく
    // このようにコピーして渡す必要があります。
    private fun reloadListView() {
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val taskRealmResults =
            mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)
        // 上記の結果を、TaskList としてセットする
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)
        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter
        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()

        //後でTask class に変更
        //val taskList = mutableListOf("aaa", "bbb", "ccc")
        //mTaskAdapter.taskList = taskList
        //listView1.adapter = mTaskAdapter
        //mTaskAdapter.notifyDataSetChanged()
    }

    //最後にonDestroyメソッドをオーバーライドしてRealmクラスのcloseメソッド
// を呼び出しています。getDefaultInstanceメソッドで取得したRealmクラスの
// オブジェクトはcloseメソッドで終了させる必要があります。onDestroyメソッド
// はActivityが破棄されるときに呼び出されるメソッドなので、
// 最後にRealmクラスのオブジェクトを破棄することになります。
    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    private fun addTaskForTest() {
        val task = Task()
        task.title = "作業"
        task.contents = "プログラムを書いてPUSHする"
        task.date = Date()
        task.id = 0
        mRealm.beginTransaction() //初回、この一文がなくてエラー
        //"Cannot modify managed objects outside of a write transaction."
        mRealm.copyToRealmOrUpdate(task)
        mRealm.commitTransaction()
    }
}


/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }*/

