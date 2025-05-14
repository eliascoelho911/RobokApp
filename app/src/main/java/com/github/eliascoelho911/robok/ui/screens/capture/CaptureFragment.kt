package com.github.eliascoelho911.robok.ui.screens.capture

import android.Manifest.permission.CAMERA
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.databinding.CaptureFragmentBinding
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.ui.screens.capture.CaptureFragmentDirections.Companion.actionCaptureFragmentToRubikCubeSolve
import com.github.eliascoelho911.robok.util.addImePadding

class CaptureFragment : Fragment() {
    private var binding: CaptureFragmentBinding? = null
    private val faceScannerView by lazy { binding!!.faceScannerView }
    private val captureButton by lazy { binding!!.fabCapture }
    private val resetButton by lazy { binding!!.fabReset }
    private val executor get() = ContextCompat.getMainExecutor(requireContext())
    private val viewModel by viewModels<CaptureViewModel>()
    private val rubikCubeInvalidAlertDialog
        get() = AlertDialog.Builder(requireContext())
            .setMessage(R.string.rubik_cube_invalid)
            .setPositiveButton(getString(R.string.re_scan).uppercase()) { dialog, _ ->
                dialog.dismiss()
            }.setOnDismissListener {
                viewModel.resetScan()
            }
    private lateinit var requestPermissionToStartScanner: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CaptureFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionToStartScanner = registerForActivityResult(RequestPermission(),
            ::startScannerIfPermissionGranted)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionToStartScanner.launch(CAMERA)
        setupClickListeners()
        setupObservers()
        captureButton.addImePadding()
        resetButton.addImePadding()
    }

    override fun onDestroy() {
        super.onDestroy()
        faceScannerView.finish()
        binding = null
    }

    private fun setupObservers() {
        viewModel.currentFaceToScan.observe(viewLifecycleOwner) {
            showHintToScanFace(it)
        }
        viewModel.scannedRubikCube.observe(viewLifecycleOwner) { it ->
            it.onSuccess { rubikCube ->
                navigateToRubikCubeSolve(rubikCube)
            }.onFailure {
                rubikCubeInvalidAlertDialog.show()
            }
        }
    }

    private fun setupClickListeners() {
        captureButton.setOnClickListener {
            scanColorsAndCreateFace()
        }
        resetButton.setOnClickListener {
            viewModel.resetScan()
        }
    }

    private fun showHintToScanFace(item: FaceScanOrderManager.Item) {
        deactivateButtons()
        with(item) {
            faceScannerView.showHintToScanFace(movesToDestination,
                directionToDestination,
                onAnimationEnd = {
                    activateButtons()
                }
            )
        }
    }

    private fun deactivateButtons() {
        captureButton.isClickable = false
        resetButton.isClickable = false
    }

    private fun activateButtons() {
        captureButton.isClickable = true
        resetButton.isClickable = true
    }

    private fun scanColorsAndCreateFace() {
        faceScannerView.scanColorsOfFace().let { colors ->
            viewModel.finishesScanningTheCurrentFace(colors)
        }
    }

    private fun startScannerIfPermissionGranted(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            faceScannerView.start(viewLifecycleOwner, executor)
            showButtons()
        } else {
            hideButtons()
        }
    }

    private fun showButtons() {
        captureButton.apply {
            show()
            isVisible = true
        }
        resetButton.apply {
            show()
            isVisible = true
        }
    }

    private fun hideButtons() {
        captureButton.hide()
        resetButton.hide()
    }

    private fun navigateToRubikCubeSolve(rubikCube: RubikCube) {
        actionCaptureFragmentToRubikCubeSolve(rubikCube).let {
            findNavController().navigate(it)
        }
    }
}