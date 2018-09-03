package jp.co.neosystem.rd.android.sample.dialogsample

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainAction(val context: Context, val vm: MainViewModel) {

    fun onClick() {

        vm.progress.postValue(true)
        val task = Observable.create<Int> {
            // 時間のかかる処理
            try{
                Thread.sleep(2000)
            }catch(ignore:InterruptedException){
            }

            // まれに失敗
            if (Math.random() < 0.5) {
                it.tryOnError(Exception("Error"))
                return@create
            }
            // 成功&終了
            it.onNext(1)
            it.onComplete()
        }.subscribeOn(
                Schedulers.computation()
        ).observeOn(
                AndroidSchedulers.mainThread()
        ).doOnDispose {
            Log.i("MainAction", "doOnDispose")
        }.doFinally {
            vm.progress.postValue(false)
        }

        showDialog(context, "処理中", "処理中です...", task, {
            Toast.makeText(context, "キャンセルしました", Toast.LENGTH_SHORT).show()
        }).subscribe({
            Toast.makeText(context, "完了しました", Toast.LENGTH_SHORT).show()
        }, {
            Toast.makeText(context, "失敗しました", Toast.LENGTH_SHORT).show()
        })
    }


    fun <T> showDialog(context: Context, title: String, message: String, observable: Observable<T>, cancelCallback:(()->Unit) = {}): Single<T> {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)

        var disposableCallback: (() -> Unit)? = null

        // ダイアログ外のタップでダイアログを閉じないようにする
        dialogBuilder.setCancelable(false)

        // キャンセルボタンを表示し、イベント割り当て
        dialogBuilder.setNegativeButton("Cancel", { v1, _ ->
            cancelCallback.invoke()
            disposableCallback?.invoke()
            v1.dismiss()
        })

        val dialog = dialogBuilder.show()

        return Observable.using({
            dialog
        }, {
            observable
        },
                Dialog::dismiss
        ).firstOrError().doOnSubscribe { disposable ->
            disposableCallback = {
                disposable.dispose()
            }
        }

    }

}