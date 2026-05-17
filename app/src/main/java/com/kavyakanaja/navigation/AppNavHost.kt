package com.kavyakanaja.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kavyakanaja.KavyaKanajaApp
import com.kavyakanaja.domain.PoemRepository
import com.kavyakanaja.ui.home.HomeScreen
import com.kavyakanaja.ui.home.HomeViewModel
import com.kavyakanaja.ui.poemlist.PoemListScreen
import com.kavyakanaja.ui.poemlist.PoemListViewModel
import com.kavyakanaja.ui.poemdetail.PoemDetailScreen
import com.kavyakanaja.ui.poemdetail.PoemDetailViewModel
import com.kavyakanaja.ui.poetlist.PoetListScreen
import com.kavyakanaja.ui.poetlist.PoetListViewModel
import com.kavyakanaja.ui.poetdetail.PoetDetailScreen
import com.kavyakanaja.ui.poetdetail.PoetDetailViewModel
import com.kavyakanaja.ui.util.factory

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val repository: PoemRepository =
        (context.applicationContext as KavyaKanajaApp).poemRepository

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            val vm: HomeViewModel = viewModel(factory = factory { HomeViewModel(repository) })
            HomeScreen(
                viewModel = vm,
                onOpenPoem = { poemId ->
                    navController.navigate(Routes.poemDetail(poemId))
                },
                onOpenPoems = {
                    navController.navigate(Routes.poemList())
                },
                onOpenPoets = {
                    navController.navigate(Routes.poetList())
                },
            )
        }
        composable(Routes.POEM_LIST) {
            val vm: PoemListViewModel = viewModel(factory = factory { PoemListViewModel(repository) })
            PoemListScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onOpenPoem = { poemId ->
                    navController.navigate(Routes.poemDetail(poemId))
                },
            )
        }
        composable(Routes.POET_LIST) {
            val vm: PoetListViewModel = viewModel(factory = factory { PoetListViewModel(repository) })
            PoetListScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onOpenPoet = { poetId ->
                    navController.navigate(Routes.poetDetail(poetId))
                },
            )
        }
        composable(
            route = Routes.POEM_DETAIL,
            arguments = listOf(navArgument("poemId") { type = NavType.StringType }),
        ) { entry ->
            val poemId = entry.arguments?.getString("poemId") ?: return@composable
            val vm: PoemDetailViewModel = viewModel(
                factory = factory {
                    PoemDetailViewModel(
                        application = application,
                        repository = repository,
                        poemId = poemId,
                    )
                },
            )
            PoemDetailScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onOpenPoet = { poetId ->
                    navController.navigate(Routes.poetDetail(poetId))
                },
            )
        }
        composable(
            route = Routes.POET_DETAIL,
            arguments = listOf(navArgument("poetId") { type = NavType.StringType }),
        ) { entry ->
            val poetId = entry.arguments?.getString("poetId") ?: return@composable
            val vm: PoetDetailViewModel = viewModel(
                factory = factory {
                    PoetDetailViewModel(
                        repository = repository,
                        poetId = poetId,
                    )
                },
            )
            PoetDetailScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
