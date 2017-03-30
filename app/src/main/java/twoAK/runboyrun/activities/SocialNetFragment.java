package twoAK.runboyrun.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.vk.sdk.VKScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twoAK.runboyrun.R;


public class SocialNetFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener {
    public static SocialNetworkManager mSocialNetworkManager;
    /**
     * SocialNetwork Ids in ASNE:
     * 1 - Twitter
     * 2 - LinkedIn
     * 3 - Google Plus
     * 4 - Facebook
     * 5 - Vkontakte
     * 6 - Odnoklassniki
     * 7 - Instagram
     */

    private Button googleplusButton;
    private Button vkButton;
    private Button facebookButton;

    public SocialNetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.socialnet_fragment, container, false);
        //((SocialNetworksAuthActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        // init buttons and set Listener
        vkButton = (Button) rootView.findViewById(R.id.socialnets_vkButton);
        vkButton.setOnClickListener(loginClick);
        facebookButton = (Button) rootView.findViewById(R.id.socialnets_fbButton);
        facebookButton.setOnClickListener(loginClick);
        googleplusButton = (Button) rootView.findViewById(R.id.socialnets_gpButton);
        googleplusButton.setOnClickListener(loginClick);

        //Get Keys for initiate SocialNetworks
        String VK_KEY = getActivity().getString(R.string.vk_app_id);

        //Chose permissions
        String[] vkScope = new String[] { VKScope.NOHTTPS };
        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile", "user_birthday"));

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SocialNetworksAuthActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager OKSocialNetwork
            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            //Init and add to manager FacebookSocialNetwork
            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SocialNetworksAuthActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }
        return rootView;
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
                case VkSocialNetwork.ID:
                    vkButton.setText("Show VK profile");
                    break;
                case FacebookSocialNetwork.ID:
                    facebookButton.setText("Show Facebook profile");
                    break;
            }
        }
    }
    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }


    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;
            switch (view.getId()){
                case R.id.socialnets_vkButton:
                    networkId = VkSocialNetwork.ID;
                    break;
                case R.id.socialnets_fbButton:
                    networkId = FacebookSocialNetwork.ID;
                    break;
            }
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                if(networkId != 0) {
                    socialNetwork.requestLogin();
                    SocialNetworksAuthActivity.showProgress("Loading social person");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                startProfile(socialNetwork.getID());
            }
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        SocialNetworksAuthActivity.hideProgress();
        startProfile(networkId);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        SocialNetworksAuthActivity.hideProgress();

        getActivity().setResult(SocialNetworksAuthActivity.RESULT_CANCELED, new Intent());
        getActivity().finish();
    }

    private void startProfile(int networkId){
        ProfileFragment profile = ProfileFragment.newInstannce(networkId);
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("profile")
                .replace(R.id.container, profile)
                .commit();

    }
}