package jp.techacademy.katsuyoshi.matsubara.taskapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

//メソッド名	実装/修正する内容
//onCreate	ActionBar、UI部品の設定。
//addTask	タスクの追加・更新。アラームの設定。

class InputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
    }
}
