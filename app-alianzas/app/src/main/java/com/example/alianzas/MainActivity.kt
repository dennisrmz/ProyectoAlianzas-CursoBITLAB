package com.example.alianzas

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), LogInFragment.OnFragmentInteractionListener,
    LogInCarnetVerification.OnFragmentInteractionListener,
    HomeFragment.OnFragmentInteractionListener, RubroFragment.OnFragmentInteractionListener,
    fragmentDetailAnuncios.OnFragmentRecyclerInteractionListener,
    ComidaFragment.OnFragmentInteractionListener,
    DetalleFragment.OnFragmentRecyclerInteractionListener,
    PersonalFragment.OnFragmentInteractionListener,
    EstablecimientoFragment.OnFragmentInteractionListener, CarnetRate.OnFragmentInteractionListener,
    RateFragment.OnFragmentInteractionListener {

    //FRAGMENT MANAGER VAL:
    private val manager = supportFragmentManager

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    println("home pressed")
                    backStackFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.map -> {
                    println("map pressed")
                    backStackFragment(RubroFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cart -> {
                    println("cart pressed")
                    backStackFragment(PersonalFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }

            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        noBackStackFragment(LogInFragment())
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


    }

    private fun noBackStackFragment(name: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container, name)

        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStackImmediate()
        }

        transaction.commit()
    }

    private fun backStackFragment(name: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container, name)
        transaction.addToBackStack("Fragment: $name")
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.salir -> {
                var firebase = FirebaseAuth.getInstance()
                Toast.makeText(this, "salir", Toast.LENGTH_SHORT).show()
                firebase.signOut()
                bottomNavigation.visibility = View.GONE
                toolbar.visibility = View.GONE
                noBackStackFragment(LogInFragment())

            }
            R.id.configuraciones -> {
//                showFragment()
            }
            else -> {
                Toast.makeText(this, "atras", Toast.LENGTH_SHORT).show()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //Fragment Interaction Methods:
    private fun showFragment(name: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container, name)
        transaction.commit()

    }

    private fun showDetailFragment(name: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container, name)
        transaction.commit()
    }

    private fun showFragmentHome() {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container, HomeFragment())
        transaction.commit()
    }

    private fun mostrarFragmentComida(name: Fragment) {
        val fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, name)
        fragmentTransaction.addToBackStack("$name")
        fragmentTransaction.commit()
    }

    private fun mostrarFragmentHome() {
        supportActionBar?.title = "Home"
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container, HomeFragment())
        transaction.commit()
    }

    private fun mostrarFragmentDetail(name: Fragment) {
        val fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, name)
        fragmentTransaction.addToBackStack("$name")
        fragmentTransaction.commit()
    }

    override fun onLogInFragmentInteraction() {
        noBackStackFragment(LogInCarnetVerification())
    }

    override fun onLogInCarnetVerificationInteraction() {}

    override fun onAlreadyGotCredentials() {
        noBackStackFragment(HomeFragment())
        bottomNavigation.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
//        supportActionBar?.setTitle("Home")
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    override fun activateBottomNavigation() {
        bottomNavigation.visibility = View.VISIBLE
    }

    //Funciones para habilitar o deshabilitar el back en el toolbar
    fun activateBackToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun disableBackToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    //Listeners para cambio de nombre del toolbar
    override fun activateHomeToolbar() {
        supportActionBar?.title = "Home"
        disableBackToolbar()
    }

    override fun activatePersonalToolbar() {
        supportActionBar?.setTitle("Personal")
        disableBackToolbar()
    }

    override fun activateRubrolToolbar() {
        supportActionBar?.setTitle("Rubros")
        disableBackToolbar()
    }

    override fun activateAnuncioDetalleToolbar() {
        supportActionBar?.setTitle("Anuncio")
        activateBackToolbar()
    }

    override fun activateRubroDetalleToolbar() {
        supportActionBar?.setTitle("Detalle")
        activateBackToolbar()
    }

    override fun activatePromocionesToolbar() {
        supportActionBar?.setTitle("Promociones")
        activateBackToolbar()
    }

    override fun activatePromocionToolbar() {
        supportActionBar?.setTitle("Promocion")
        activateBackToolbar()
    }


    override fun onFragmentRecyclerInteraction(anuncioImage: String, anuncioDetail: String) {
        backStackFragment(fragmentDetailAnuncios.newInstance(anuncioImage, anuncioDetail))
    }

    override fun onFragmentRecyclerInteractionRubro(query: String) {
        backStackFragment(EstablecimientoFragment.newInstance(query))
    }

    override fun onFragmentDetailInteraction() {
        bottomNavigation.visibility = View.GONE
        backStackFragment(CarnetRate())
        supportActionBar?.title = "Carnet"
    }

    override fun onFragmentRecyclerInteraction(
        Imagen: String,
        Nombre: String,
        Descripcion: String,
        Fecha: String
    ) {
        backStackFragment(DetalleFragment.newInstance(Nombre, Imagen, Descripcion))
    }

    override fun onFragmentRecyclerEstabInteraction(child: String?, query: String?) {
        backStackFragment(ComidaFragment.newInstance(child!!, query!!))
    }

    override fun onCarnetRateFragmentInteraction() {
        disableBackToolbar()
        backStackFragment(RateFragment())
        supportActionBar?.title = "Calificacion"
    }

    override fun onRateFragmentInteraction() {
        activateBottomNavigation()
        noBackStackFragment(HomeFragment())
    }
}
