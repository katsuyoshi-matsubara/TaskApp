package jp.techacademy.katsuyoshi.matsubara.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

//モデルとはデータを表現するもので、TaskAapではTaskが相当することになります。
// Realmのモデルは、RealmObjectクラスを継承したKotlinのクラスとして定義します。
// Serializableインターフェイスを実装することで生成したオブジェクトを
// シリアライズすることができるようになります。シリアライズとはデータを丸ごと
// ファイルに保存したり、TaskAppでいうと別のActivityに渡すことができるように
// することです。またopen修飾子を付けるのは、Realmが内部的にTaskを継承した
// クラスを作成して利用するためです。
open class Task : RealmObject(), Serializable {
    var title: String = ""
    var contents: String = ""
    var date: Date = Date()

    //ユニークなIDを指定するidを定義し、@PrimaryKeyと付けます。
    // @PrimaryKeyはRealmがプライマリーキーと判断するために必要なものです。
    // プライマリーキーとは主キーとも呼ばれ、データーベースの一つの
    // テーブルの中でデータを唯一的に確かめるための値です。
    @PrimaryKey
    var id: Int = 0
}