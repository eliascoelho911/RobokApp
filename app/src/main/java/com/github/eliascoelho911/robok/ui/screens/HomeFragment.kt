package com.github.eliascoelho911.robok.ui.screens

import android.Manifest.permission.CAMERA
import android.graphics.Color
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
import com.github.eliascoelho911.robok.domain.RubikCube
import com.github.eliascoelho911.robok.domain.RubikCubeColor
import com.github.eliascoelho911.robok.ui.animation.openWithAnimation
import com.github.eliascoelho911.robok.ui.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.capture
import kotlinx.android.synthetic.main.home_fragment.grid_scanner
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
        grid_scanner.closeCamera()
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            grid_scanner.run {
                columns = RubikCube.NumberOfColumnsOnTheSide
                rows = RubikCube.NumberOfRowsOnTheSide
                startCamera(viewLifecycleOwner, _executor)
            }
            capture.openWithAnimation()
        }
    }

    private fun clickListeners() {
        grid_scanner.onClickStartScan = {
            _requestPermissionToStartCamera.launch(CAMERA)
        }
        capture.setOnClickListener {
            grid_scanner.lookForTheGridColors(_executor, onFound = { colors ->
                colors.tintGridItems()
            }, onFailure = {
                Toast.makeText(requireContext(),
                    getString(R.string.error_capture_cube_face),
                    Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun List<Color>.tintGridItems() {
        forEachIndexed { index, color ->
            val rubikCubeColor = RubikCubeColor.findBySimilarity(requireContext(), color)
            val newColor = rubikCubeColor.androidColor(requireContext())
            grid_scanner.tintItem(index, newColor, onAnimationEnd = {
                grid_scanner.removeItemColor(index)
            })
        }
    }

    private val _executor get() = ContextCompat.getMainExecutor(requireContext())
    private val _viewModel: HomeViewModel by viewModel()
    private lateinit var _requestPermissionToStartCamera: ActivityResultLauncher<String>
}