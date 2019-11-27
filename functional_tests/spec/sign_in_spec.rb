require "spec_helper"
feature "sign in" do
  scenario "I should be able to visit the sign in page" do
    visit '/sign_in'
    expect(page).to have_content 'Sign In to Battle!'
  end
end
