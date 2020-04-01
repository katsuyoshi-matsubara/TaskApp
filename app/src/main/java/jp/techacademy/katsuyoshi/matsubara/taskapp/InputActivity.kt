package jp.techacademy.katsuyoshi.matsubara.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import java.util.*

//メソッド名	実装/修正する内容
//onCreate	ActionBar、UI部品の設定。
//addTask	タスクの追加・更新。アラームの設定。

class InputActivity : AppCompatActivity() {

    private var mYear = 0 // m の意味？
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mTask: Task? = null

    private  val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
                date_button.text = dateString
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private val mOnTimeClickListener = View.OnClickListener {
        val timePickerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener{ _, hour, minute ->
                mHour = hour
                mMinute = minute
                val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)
                times_button.text = timeString
            }, mHour, mMinute, false)
        timePickerDialog.show()
    }
//決定ボタンをクリックしたときに呼ばれるmOnDoneClickListenerでは、
// addTaskメソッドでRealmに保存/更新したあと、finishメソッドを
// 呼び出すことでInputActivityを閉じて前の画面（MainActivity）に戻ります。
    private val mOnDoneClickListener = View.OnClickListener {
        addTask()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        //Actionbar を設定
        // setSupportActionBarメソッドにより、ツールバーをActionBarとして
        // 使えるように設定しています。また、setDisplayHomeAsUpEnabled
        // メソッドで、ActionBarに戻るボタンを表示しています。その、
        // 他のUI部品の設定を行います。
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        //UI部品の設定
        date_button.setOnClickListener(mOnDateClickListener)
        times_button.setOnClickListener(mOnTimeClickListener)
        done_button.setOnClickListener(mOnDoneClickListener)

        // EXTRA_TASK から Task の id を取得して、
        // id から Task のインスタンスを取得する
        //クラス（ファイル）の垣根を超えて、Task を渡す必要が出てきます。
        // そのとき活躍するのが Intent です。Intent によって Task の id
        // を渡したり (putExtra())、受け取ったり (getIntExtra()) します。
        // Realm のドキュメントにも、Intent による Realm オブジェクトの
        // 渡し方 が載っています。ここでは既に渡し終わっていると仮定して
        // （後ほど渡す方も学びます）、受け取る方から実装していきます。
        val intent = intent
        //EXTRA_TASK から Task の id を取り出します。
        // このとき、もし EXTRA_TASK が設定されていないと
        // taskId には第二引数で指定している既定値 -1 が代入されます。
        val taskId = intent.getIntExtra(EXTRA_TASK, -1)
        val realm = Realm.getDefaultInstance()
        // Task の id が taskId のものが検索され、findFirst() によって
        // 最初に見つかったインスタンスが返され、 mTask へ代入されます。
        // このとき、 taskId に -1 が入っていると、検索に引っかからず、
        // mTask には null が代入されます。これは addTask で指定している、
        // id が必ず 0 以上というアプリの仕様を利用しています。
        //それでは、EXTRA_TASK が設定されていない、すなわち taskId が -1
        // になるのはどのような場合を想定しているのでしょう。それはタスクを
        // 新規作成する場合です。新規作成の場合は遷移元であるMainActivityから
        // EXTRA_TASK は渡されないので taskId に -1 が代入され、 mTask
        // には null が代入されます。また mTask がnullであれば、
        // カレンダーから、現在時刻をmYear、mMonth、mDay、mHour、mMinut
        // に設定します。MainActivityから EXTRA_TASK が渡ってきた場合は更新のため、
        // 渡ってきたタスクの時間を設定します。
        mTask = realm.where(Task::class.java).equalTo("id", taskId).findFirst()
        realm.close()

        if (mTask == null) {
            //新規作成の場合
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)
        } else {
            //更新の場合
            title_edit_text.setText(mTask!!.title)
            content_edit_text.setText(mTask!!.contents)

            val calendar = Calendar.getInstance()
            calendar.time = mTask!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

            val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
            val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)

            date_button.text = dateString
            times_button.text = timeString
        }
    }

    private fun addTask() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
//mTaskがnull、つまり新規作成の場合はTaskクラスを生成し、保存されている
// タスクの中の最大のidの値に1を足したものを設定します。こうすることで
// ユニークなIDを設定することが可能となります。そしてタイトル、内容、
// 日時をmTaskに設定し、データベースに保存します。
        if (mTask == null) {
            //新規作成の場合
            mTask = Task()

            val taskRealmResults = realm.where(Task::class.java).findAll()

            val identifier : Int =
                if (taskRealmResults.max("id") != null) {
                    taskRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mTask!!.id = identifier
        }
        val title = title_edit_text.text.toString()
        val content = content_edit_text.text.toString()

        mTask!!.title = title
        mTask!!.contents = content
        val calendar = GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute)
        val date = calendar.time
        mTask!!.date = date
//Realmでデータを追加、削除など変更を行う場合はbeginTransactionメソッドを
// 呼び出し、削除などの処理、そして最後にcommitTransactionメソッドを
// 呼び出す必要があります。データの保存・更新はcopyToRealmOrUpdateメソッドを
// 使います。これは引数で与えたオブジェクトが存在していれば更新、
// なければ追加を行うメソッドです。最後にcloseメソッドを呼び出します。
        realm.copyToRealmOrUpdate(mTask!!)
        realm.commitTransaction()

        realm.close()
    }
}
