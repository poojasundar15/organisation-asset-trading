//package Client;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Assets are available to be purchased or sold on the electronic marketplace, with
// * users spending credits to obtain said assets.
// */
//public class Assets {
//
//    // List of asset types available to be bought or sold
//    private List<String> assets;
//    // Asset type
//    private String assetName;
//
//    /**
//     * Constructor of the asset classes that initialises the list of assets and adds the default
//     * assets available onto the marketplace.
//     */
//    public Assets() {
//        assets = new ArrayList<>();
//
//        // Default assets available on the marketplace
//        assets.add("Computational Resources");
//        assets.add("Hardware Resources");
//        assets.add("Software Licenses");
//    }
//
//    /**
//     * Prevents an unapproved asset from being added to an organisation's list of assets
//     *
//     * @param assetName name of the asset
//     * @return true if asset is approved, false if otherwise
//     */
//    public boolean checkAsset(String assetName) {
//        return assets.contains(assetName);
//    }
//
//    /**
//     * Checks against every member of an organisation's list of assets to see if there are
//     * unapproved assets in the list
//     *
//     * @param assetList List of assets
//     * @return True if all assets asset is approved, false if otherwise
//     */
//    public boolean checkAssetList(List<String> assetList) {
//        for (String s : assetList) {
//            if (!(checkAsset(s))) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Allows admin users (IT Staff) to create a new asset on the marketplace
//     *
//     * @param user
//     * @param assetName
//     * @throws UserException - if the user is not an admin
//     * @throws AssetsException - if the asset already exists
//     */
//    public void createAsset(User user, String assetName) throws UserException, AssetsException {
//        boolean isAdmin = user.getAdminStatus();
//        boolean assetExists = assets.contains(assetName);
//
//        if(isAdmin && !assetExists) {
//            assets.add(assetName);
//        }
//        else if(!isAdmin) {
//            throw new UserException("User must have admin rights to add an asset.");
//        }
//        else if(assetExists) {
//            throw new AssetsException("Asset already exists.");
//        }
//    }
//
//    /**
//     * Gets a list of all assets available to be bought or sold
//     *
//     * @return list of assets
//     */
//    public List<String> getAllAssets() {
//        return assets;
//    }
//}
