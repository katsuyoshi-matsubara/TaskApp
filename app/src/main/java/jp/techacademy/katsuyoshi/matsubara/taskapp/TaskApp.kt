package jp.techacademy.katsuyoshi.matsubara.taskapp

import android.app.Application
import io.realm.Realm

//モデルクラスを用意したら、そのモデルと接続するRealmデータベースの準備をします。
//Applicationクラスを継承したTaskAppクラスを作成。onCreateメソッドをオーバーライドします。
// その中でRealm.init(this)をしてRealmを初期化します。
class TaskApp: Application() {
    override  fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}
//Applicationクラスを継承したTaskAppクラスを作成するだけではこのクラスは
// 使われることはないため、AndroidManifest.xmlに１行追加する必要があります。
// application要素に android:name=".TaskApp"を追記します。
// これはこのアプリのApplicationクラスはこれですよ、と指定するためのものです。
// ここで指定したクラスのonCreateメソッドがアプリ起動時に呼ばれます。