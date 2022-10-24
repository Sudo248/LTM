package com.sudo248.ltm.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sudo248.ltm.databinding.DialogLoadingBinding


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 14:57 - 23/10/2022
 */
class LoadingDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogLoadingBinding.inflate(inflater,container,false)
        isCancelable = false
        return binding.root
    }
}