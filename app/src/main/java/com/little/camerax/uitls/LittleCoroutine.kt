package com.little.camerax.uitls

import kotlinx.coroutines.*

/**
 * Kotlin协程coroutine学习
 */
object LittleCoroutine {
    fun testCoroutine() {
        println("Kotlin协程coroutine学习")

        var viewModelJob = Job()

        var uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)   // 初始化CoroutineScope, 指定协程运行的线程, 传入job方便以后取消协程

        // 协程运行在Dispatchers.IO线程
        uiScope.launch {
            // 启动一个协程
            println("coroutine before...")
            doSomething()      // suspend函数运行在协程内或另一个suspend函数内
            println("coroutine after...")
        }

        println("coroutine 有返回值的协程。。。")
        var asyncJob = Job()
        var asyncScope = CoroutineScope(Dispatchers.Default + asyncJob)
        asyncScope.launch {
            println("coroutine async/await before...")
            var deferred = asyncScope.async {
                doOtherThing()
            }
            println("coroutine async/await after...")
            println(deferred.await())   // 暂停协程的执行, 等待async的完成
            println("coroutine async/await finish...")
        }
    }

    private suspend fun doSomething() {
        delay(3000L)    // delay是一个suspend函数
        println("Thread Name is ${Thread.currentThread().name}, Hello, from coroutines!")
    }

    private suspend fun doOtherThing(): String {
        delay(2000)
        return "Thread Name is ${Thread.currentThread().name}, Hello coroutines!"
    }
}