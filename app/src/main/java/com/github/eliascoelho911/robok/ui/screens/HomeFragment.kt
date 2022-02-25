package com.github.eliascoelho911.robok.ui.screens

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.ui.viewmodels.HomeViewModel
import com.github.eliascoelho911.robok.util.showToast
import kotlinx.android.synthetic.main.home_fragment.rubik_cube_side_scanner
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.home_fragment, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionToStartCamera = registerForActivityResult(RequestPermission()) {
            startCamera(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionToStartCamera.launch(CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        rubik_cube_side_scanner.finish()
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            rubik_cube_side_scanner.start(viewLifecycleOwner, executor, onSideCaptured = {
            }, onError = { showScanCubeSideError() })
        }
    }


    private fun showScanCubeSideError() {
        requireContext().showToast(getString(R.string.error_capture_cube_side))
    }

    private val executor get() = ContextCompat.getMainExecutor(requireContext())
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var requestPermissionToStartCamera: ActivityResultLauncher<String>
}
