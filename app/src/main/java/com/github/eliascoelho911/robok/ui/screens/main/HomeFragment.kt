package com.github.eliascoelho911.robok.ui.screens.main

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.ui.animation.openWithAnimation
import kotlinx.android.synthetic.main.home_fragment.capture
import kotlinx.android.synthetic.main.home_fragment.rubiks_cube_scanner
import kotlinx.android.synthetic.main.rubiks_cube_scanner.grid
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.home_fragment, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _requestPermissionToStartCamera = registerForActivityResult(RequestPermission()) {
            startCamera(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        rubiks_cube_scanner.closeCamera()
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            rubiks_cube_scanner.startCamera(viewLifecycleOwner,
                executor = _executor)
            capture.openWithAnimation()
        }
    }

    private fun clickListeners() {
        rubiks_cube_scanner.onClickStartScan = {
            _requestPermissionToStartCamera.launch(CAMERA)
        }
        capture.setOnClickListener {
            rubiks_cube_scanner.lookForTheCubeFace(_executor, onFound = {
                it.forEachIndexed { index, color ->
                    grid.getChildAt(index).setBackgroundColor(color)
                }
            }, onFailure = {
                Toast.makeText(requireContext(),
                    getString(R.string.error_capture_cube_face),
                    Toast.LENGTH_SHORT).show()
            })
        }
    }

    private val _executor get() = ContextCompat.getMainExecutor(requireContext())
    private val _viewModel: HomeViewModel by viewModel()
    private lateinit var _requestPermissionToStartCamera: ActivityResultLauncher<String>
}