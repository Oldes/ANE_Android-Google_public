package com.amanitadesign;

import android.util.Log;

import com.adobe.fre.FREByteArray;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;

import java.nio.ByteBuffer;

public class GoogleApiFunctions {
    static public class IsGameHelperAvailableFunction implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            try {
                return FREObject.newObject(
                        GoogleExtension.googleApiHelper.isSignInAvailable() );
            } catch (FREWrongThreadException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    static public class SignInFunction implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            GoogleExtension.googleApiHelper.signIn();
            return null;
        }
    }
    static public class SignOutFunction implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            GoogleExtension.googleApiHelper.signOut();
            return null;
        }
    }
    static public class IsSignedInFunction implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            try {
                return FREObject.newObject(GoogleExtension.googleApiHelper.isSignedIn());
            } catch (FREWrongThreadException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static public class ReportAchievementFunction implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            try
            {
                String id = arg1[0].getAsString();
                double percent = arg1[1].getAsDouble();
                GoogleExtension.googleApiHelper.reportAchievement(id, percent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
    static public class ShowAchievementsFunction implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            try
            {
                String id = arg1[0].getAsString();
                double percent = arg1[1].getAsDouble();
                GoogleExtension.googleApiHelper.reportAchievement(id, percent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    static public class OpenSnapshot implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            FREByteArray freByteArray = null;
            try {
                String name = arg1[0].getAsString();
                if(name != null)
                    GoogleExtension.googleApiHelper.openSnapshot(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static public class ReadSnapshot implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            FREByteArray freByteArray = null;
            try {
                String name = arg1[0].getAsString();
                if(name == null) return null;

                SavedGame save = GoogleExtension.googleApiHelper.readSnapshot(name);
                if(save != null) {
                    byte[] saveData = save.getData();
                    if(saveData != null) {
                        freByteArray = FREByteArray.newByteArray();
                        freByteArray.setProperty("length", FREObject.newObject(saveData.length));
                        freByteArray.acquire();
                        ByteBuffer bytes = freByteArray.getBytes();
                        bytes.put(saveData, 0, saveData.length);
                        freByteArray.release();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return freByteArray;
        }
    }

    static public class WriteSnapshot implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            GoogleExtension.extensionContext.createHelperIfNeeded(arg0.getActivity());

            try {
                String name = arg1[0].getAsString();
                FREByteArray data = (FREByteArray) arg1[1];
                if(name == null || data == null) return null;

                long time = (long) arg1[2].getAsDouble();

                data.acquire();
                ByteBuffer bb = data.getBytes();
                byte[] bytes = new byte[(int) data.getLength()];
                bb.get(bytes);

                //Log.d("WriteSnapshot", "name: "+ name +" bytes: "+ bytes.length+" time: "+time);
                data.release();
                GoogleExtension.googleApiHelper.saveSnapshot(name, bytes, time);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    static public class DeleteSnapshot implements FREFunction {
        @Override
        public FREObject call(FREContext arg0, FREObject[] arg1) {
            try {
                String name = arg1[0].getAsString();
                if(name != null)
                    GoogleExtension.googleApiHelper.deleteSnapshot(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
