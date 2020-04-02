package jp.techacademy.katsuyoshi.matsubara.taskapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import io.realm.Realm

//ブロードキャストとは特定のアプリに向けてIntentを発行するのでなく、システム
// 全体に発行する仕組みです。受け取る側が「このIntentに反応する」というように
// 設定しておき、そのブロードキャストを受け取ります。そしてBroadcastReceiverは
// そのブロードキャストされた暗黙的Intentに応答するための仕組みです。また、
// BroadcastReceiverは明示的Intentも受け取ることもできます。今回は自分の
// アプリに向かって投げると分かっているため、明示的Intentを使います。
// BroadcastReceiverは極めてシンプルな構造で、Intentを受け取った時に
// onReceiveメソッドが呼び出されるだけです。また、PendingIntentとは
// Intentの一種で、すぐに発行するのではなく特定のタイミングで後から
// 発行させるIntentです

//通知はNotificationクラスを作成して、NotificationManagerにセットすることで
// 表示することが出来ます。アラームを受け取ったときに通知を出したいので
// TaskAlarmReceiverに処理を実装します。Notificationは
// NotificationCompat.Builderクラスを使って作成します。
//Builderクラスの主なメソッドは以下の通りです。
//
//メソッド名	内容
//setSmallIcon	ステータスバーに表示されるアイコンのリソースを設定する。
//setLargeIcon	通知に表示する大きなアイコンをBitmapで指定する。
// 指定されていない場合はsetSmallIconメソッドで指定したリソースが使われる。
//setWhen	いつ表示するか指定する。
//setDefaults	通知時の音・バイブ・ライトについて指定する。
//setAutoCancel	trueの場合はユーザがタップしたら通知が消える。
// falseの場合はコード上で消す必要がある。
//setTicker	ステータスバーに流れる文字を指定する。5.0以降では表示されない。
//setContentTitle	アイコンの横に太文字で表示される文字列を指定する。
//setContentText	contentTitleの下に表示される文字列を指定する。
//setContentIntent	ユーザが通知をタップしたときに起動するIntentを指定する。
//なお、SDKバージョン26(Oreo)以上から通知の仕組みが変わり、通知を細かく
// 識別するチャネルの設定が必要となりました。ソースコードで
// 「Build.VERSION.SDK_INT >= 26」と判定して記述している部分です
class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //SDK version が26以上の場合、チャネルを設定する必要がある
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("default",
                "channel name",
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel description"
            notificationManager.createNotificationChannel(channel)
        }
        //通知の設定を行う
        val builder = NotificationCompat.Builder(context, "default")
        builder.setSmallIcon(R.drawable.small_icon)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.large_icon))
        builder.setWhen(System.currentTimeMillis())
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setAutoCancel(true)

        //EXTRA_TASKからTaskのidを取得してidからTaskのインスタンスを取得する
        val taskId = intent!!.getIntExtra(EXTRA_TASK, -1)
        val realm = Realm.getDefaultInstance()
        val task = realm.where(Task::class.java).equalTo("id", taskId).findFirst()

        // タスクの情報を設定する
        builder.setTicker(task!!.title)// 5.0以降は表示されない
        builder.setContentTitle(task.title)
        builder.setContentText(task.contents)
        builder.setContentText(task.category)

        // 通知をタップしたらアプリを起動するようにする
        //setContentIntentメソッドで指定するIntentはアラームのときと同じように
        // Intentを作成し、そのIntentを指定して作成したPendingIntentです。
        val startAppIntent = Intent(context, MainActivity::class.java)
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val pendingIntent = PendingIntent.getActivity(context, 0, startAppIntent, 0)
        builder.setContentIntent(pendingIntent)

        // 通知を表示する
        notificationManager.notify(task!!.id, builder.build())
        realm.close()

        //後ほど通知を表示するように修正
        //Log.d("TaskApp", "onReceive")
    }
}