package twoAK.runboyrun.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.vk.VkSocialNetwork;

import twoAK.runboyrun.R;


public class ProfileFragment extends Fragment implements OnRequestSocialPersonCompleteListener {

    private static final String NETWORK_ID = "NETWORK_ID";

    private SocialNetwork socialNetwork;
    private int networkId;
    private RelativeLayout frame;

    public static ProfileFragment newInstannce(int _id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(NETWORK_ID, _id);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        networkId = getArguments().containsKey(NETWORK_ID) ? getArguments().getInt(NETWORK_ID) : 0;
        //((SocialNetworksAuthActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        frame = (RelativeLayout) rootView.findViewById(R.id.frame);


        socialNetwork = SocialNetFragment.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        socialNetwork.requestCurrentPerson();

        SocialNetworksAuthActivity.showProgress("Loading social person");
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.social_nets, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                socialNetwork.logout();
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestSocialPersonSuccess(int networkId, SocialPerson socialPerson) {
        SocialNetworksAuthActivity.hideProgress();

        String name = socialPerson.name.substring(0, socialPerson.name.indexOf(" "));
        String surname = socialPerson.name.substring(socialPerson.name.indexOf(" ")+1, socialPerson.name.length());

        Intent returnIntent = new Intent();
        switch (networkId) {
            case VkSocialNetwork.ID:
                returnIntent.putExtra("oauth", "vk");
                break;
            case FacebookSocialNetwork.ID:
                returnIntent.putExtra("oauth", "facebook");
                break;
        }

        returnIntent.putExtra("id", socialPerson.id);
        returnIntent.putExtra("access_token", socialNetwork.getAccessToken().token);
        returnIntent.putExtra("name", name);
        returnIntent.putExtra("surname", surname);
        getActivity().setResult(SocialNetworksAuthActivity.RESULT_OK, returnIntent);

        socialNetwork.logout();
        getActivity().finish();
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        SocialNetworksAuthActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            Toast.makeText(getActivity(), "Sent", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Toast.makeText(getActivity(), "Error while sending: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    };


    private AlertDialog.Builder alertDialogInit(String title, String message){
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(true);
        return ad;
    }
}