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
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants
import com.github.eliascoelho911.robok.domain.RubikCubeSideColor
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.domain.SidePosition
import com.github.eliascoelho911.robok.ui.animation.closeWithAnimation
import com.github.eliascoelho911.robok.ui.animation.openWithAnimation
import com.github.eliascoelho911.robok.ui.viewmodels.HomeViewModel
import com.github.eliascoelho911.robok.util.toMatrix
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
        _requestPermissionToStartCamera.launch(CAMERA)
        clickListeners()
        observers()
    }

    private fun observers() {
        scannedRubikCubeObserver()
    }

    private fun scannedRubikCubeObserver() {
        _viewModel.scannedRubikCube.observe(viewLifecycleOwner) {
            if (it.sides.size == RubikCubeConstants.NumberOfSides)
                capture.closeWithAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        grid_scanner.closeCamera()
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            grid_scanner.startCamera(viewLifecycleOwner, _executor)
            capture.openWithAnimation()
        }
    }

    private fun clickListeners() {
        capture.setOnClickListener {
            onClickCaptureListener()
        }
    }

    private fun onClickCaptureListener() {
        capture.isClickable = false
        grid_scanner.lookForTheGridColors(_executor, onFound = { colors ->
            colors.toRubikCubeSideColor().createAndAddScannedSize()
        }, onFailure = {
            Toast.makeText(requireContext(),
                getString(R.string.error_capture_cube_face),
                Toast.LENGTH_SHORT).show()
        })
    }

    private fun List<RubikCubeSideColor>.createAndAddScannedSize() {
        val matrix = toMatrix(
            width = RubikCubeConstants.LineHeight,
            height = RubikCubeConstants.LineHeight
        )
        val position = _lastSideScanned?.position?.next() ?: SidePosition.first()
        _viewModel.addScannedSide(RubikCubeSide(position, matrix))
    }

    private fun List<Color>.toRubikCubeSideColor() = map {
        RubikCubeSideColor.findBySimilarity(requireContext(), it)
    }

    private val _executor get() = ContextCompat.getMainExecutor(requireContext())
    private val _viewModel: HomeViewModel by viewModel()
    private lateinit var _requestPermissionToStartCamera: ActivityResultLauncher<String>
    private val _lastSideScanned get() = _viewModel.scannedRubikCube.value?.sides?.lastOrNull()
}
