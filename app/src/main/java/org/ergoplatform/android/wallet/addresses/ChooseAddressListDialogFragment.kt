package org.ergoplatform.android.wallet.addresses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.ergoplatform.android.AppDatabase
import org.ergoplatform.android.databinding.FragmentChooseAddressDialogBinding
import org.ergoplatform.android.databinding.FragmentChooseAddressDialogItemBinding
import org.ergoplatform.android.wallet.WalletAddressDbEntity
import org.ergoplatform.android.wallet.WalletDbEntity
import org.ergoplatform.android.wallet.getSortedDerivedAddressesList

/**
 * Let the user choose a derived address
 */
class ChooseAddressListDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_WALLET_ID = "ARG_WALLET_ID"
        private const val ARG_SHOW_ALL_ADDRESSES = "ARG_SHOW_ALL"

        fun newInstance(
            walletId: Int,
            addShowAllEntry: Boolean = false
        ): ChooseAddressListDialogFragment {
            val addressChooser = ChooseAddressListDialogFragment()
            val args = Bundle()
            args.putInt(ARG_WALLET_ID, walletId)
            args.putBoolean(ARG_SHOW_ALL_ADDRESSES, addShowAllEntry)
            addressChooser.arguments = args
            return addressChooser
        }
    }

    private var _binding: FragmentChooseAddressDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChooseAddressDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager =
            LinearLayoutManager(context)

        val context = requireContext()
        lifecycleScope.launch {
            AppDatabase.getInstance(context).walletDao()
                .loadWalletWithStateById(requireArguments().getInt(ARG_WALLET_ID))?.let {
                    binding.list.adapter = DisplayAddressesAdapter(
                        it,
                        requireArguments().getBoolean(ARG_SHOW_ALL_ADDRESSES) && it.addresses.size > 1
                    )
                }
        }
    }

    private fun onChooseAddress(addressDerivationIdx: Int?) {
        (parentFragment as AddressChooserCallback).onAddressChosen(addressDerivationIdx)
        dismiss()
    }

    private inner class ViewHolder(val binding: FragmentChooseAddressDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindAddress(address: WalletAddressDbEntity, wallet: WalletDbEntity) {
            binding.addressInformation.fillAddressInformation(address, wallet)
            binding.root.setOnClickListener {
                onChooseAddress(address.derivationIndex)
            }
        }

        fun bindAllAddresses(wallet: WalletDbEntity) {
            binding.addressInformation.fillWalletAddressesInformation(wallet)
            binding.root.setOnClickListener {
                onChooseAddress(null)
            }
        }

    }

    private inner class DisplayAddressesAdapter(
        val wallet: WalletDbEntity,
        val showAllAddresses: Boolean
    ) :
        RecyclerView.Adapter<ViewHolder>() {

        val addresses = wallet.getSortedDerivedAddressesList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentChooseAddressDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (!showAllAddresses) {
                val address = addresses.get(position)
                holder.bindAddress(address, wallet)
            } else if (position > 0) {
                val address = addresses.get(position - 1)
                holder.bindAddress(address, wallet)
            } else {
                holder.bindAllAddresses(wallet)
            }
        }

        override fun getItemCount(): Int {
            return if (showAllAddresses) addresses.size + 1 else addresses.size
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface AddressChooserCallback {
    fun onAddressChosen(addressDerivationIdx: Int?)
}