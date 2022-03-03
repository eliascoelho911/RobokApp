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
import com.github.eliascoelho911.robok.ui.viewmodels.CaptureViewModel
import com.github.eliascoelho911.robok.util.showToast
import kotlinx.android.synthetic.main.capture_fragment.side_scanner_view
import org.koin.androidx.viewmodel.ext.android.viewModel

class CaptureFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.capture_fragment, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionToStartCamera = registerForActivityResult(RequestPermission(),
            ::startCameraIfPermissionGranted)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionToStartCamera.launch(CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        side_scanner_view.finish()
    }

    private fun startCameraIfPermissionGranted(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            side_scanner_view.start(viewLifecycleOwner, executor, onSideCaptured = {

            }, onFinish = {

            })
        }
    }

    private val executor get() = ContextCompat.getMainExecutor(requireContext())
    private val viewModel: CaptureViewModel by viewModel()
    private lateinit var requestPermissionToStartCamera: ActivityResultLauncher<String>
}
