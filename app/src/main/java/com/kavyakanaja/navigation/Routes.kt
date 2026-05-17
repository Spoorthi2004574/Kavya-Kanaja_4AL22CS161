package com.kavyakanaja.navigation

object Routes {
    const val HOME = "home"
    const val POEM_LIST = "poems"
    const val POEM_DETAIL = "poem/{poemId}"
    const val POET_LIST = "poets"
    const val POET_DETAIL = "poet/{poetId}"

    fun poemList(): String = POEM_LIST
    fun poemDetail(poemId: String): String = "poem/$poemId"

    fun poetList(): String = POET_LIST
    fun poetDetail(poetId: String): String = "poet/$poetId"
}
